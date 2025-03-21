import math
from operator import attrgetter  #function which will fetch the attribute from each object
from time import sleep
import robot
import smbus2

#initialise robot class
r = robot.Robot(max_motor_voltage=12)

#Initialise servo I2C communication
bus = smbus2.SMBus(1)

#Initialise servos
r.servos[0].mode = robot.PWM_SERVO 
r.servos[1].mode = robot.PWM_SERVO 

#Initialise IR sensors GPIO
r.gpio[0].mode = robot.INPUT #front IR sensor
r.gpio[1].mode = robot.INPUT #claw IR sensor


#define starting position
selfpos = [0,-3,90]  #does this need to be the camera, or the middle of the robot?

#get team name
team = r.zone 

# dictionary to store vals for each team
team_values = {
    robot.TEAM.RUBY:    (20, 40, 50, 0),
    robot.TEAM.DIAMOND: (23, 41, 51, 1),   # (gem_id1, lairmarker_id)
    robot.TEAM.JADE:    (21, 42, 52, 2),  #not sure if "robot.TEAM.RUBY" can be stored like this. 
    robot.TEAM.TOPAZ:   (22, 43, 53, 3),
}

def find_team_vals():
    global team, gem_id1, gem_id2, lairmarker_id
    #gets values from dictionary
    gem_id1, gem_id2, lairmarker_id, team= team_values.get(team, (None, None, None))  #still sets them to none if it doesn't fit the 

find_team_vals()

def rotate_tags(tags, zone):
    rotation = (zone + 2) % 4
    for tag in tags:
        for i in range(rotation):
            x = tag[0]
            y = tag[1]
            tag[0] = -1*y
            tag[1] = x
        tag[2] += 90*rotation
        tag[2] %= 360

wall_tags = [ #default for zone 2
    [-2.5, 3, 90],
    [-1.5, 3, 90],
    [-0.5, 3, 90],
    [0.5, 3, 90],
    [1.5, 3, 90],
    [2.5, 3, 90],
    [3, 2.5, 0],
    [3, 1.5, 0],
    [3, 0.5, 0],
    [3, -0.5, 0],
    [3, -1.5, 0],
    [3, -2.5, 0],
    [2.5, -3, 270],
    [1.5, -3, 270],
    [0.5, -3, 270],
    [-0.5, -3, 270],
    [-1.5, -3, 270],
    [-2.5, -3, 270],
    [-3, -2.5, 180],
    [-3, -1.5, 180],
    [-3, -0.5, 180],
    [-3, 0.5, 180],
    [-3, 1.5, 180],
    [-3, 2.5, 180],
]

def compute_vector(dist, angle):
    vector = [round(dist * math.cos(math.radians(angle)), 4), round(dist * math.cos(math.radians(90-angle)), 4)]
    return vector

def arena_update(distance, bearing, rotation, id):
    global selfpos
    id -= 100
    tag_normal = wall_tags[id][2]
    print(tag_normal)
    print(bearing)
    print(rotation)
    selfpos[2] = tag_normal + bearing - rotation
    selfpos[2] %= 360
    vector_to_tag = compute_vector(distance, tag_normal - rotation)
    print(vector_to_tag)
    selfpos[0] = wall_tags[id][0] - vector_to_tag[0]
    selfpos[1] = wall_tags[id][1] - vector_to_tag[1]

def pos_update(distance, angle):
    global selfpos
    coords = compute_vector(distance, selfpos[2])
    selfpos[0] += coords[0]
    selfpos[1] += coords[1]
    selfpos[2] += angle
    selfpos[2] %= 360
    selfpos[2] = round(selfpos[2], 4)

markers = []
gemlist = []  # most valuable. List of gem objects
othergemlist = [] # second most valuable
sheeplist = [] #third most valuable 

#coord_memory = [] #stores coordinates of any boxes we have seen

def dist_calc(dist, rot, bear): #calculate dist. to centre of the box. Only requires one marker. 
    return (dist/math.sin(90 + rot)) * (math.sin(90 - bear - rot))

def sort_markers(markers): #check if this works....
    #removes duplicates
    seen_ids = set()
    uniq_markers = []   #final list of unique markers   
    for marker in markers:
        marker_id = marker.info.id
        if marker_id not in seen_ids:
            seen_ids.add(marker_id)
            uniq_markers.append(marker)
            marker.dist = dist_calc(marker.dist, marker.rotation.x, marker.bearing.y)  #calculate new distance to centre
    print(f"Processing marker: ID={marker.info.id}, Dist={marker.dist}")
    return uniq_markers

def analyse():
    markers = r.see()
    #priority goes to gems first. For each sheep, priority goes to the closest one it sees 
    if len(markers) > 1: #only sort through the list if the list has more than 1 value. 
        print("more than 1 marker")
        for i in range(len(markers)):
            print(markers[i]) #debug

            #arena tags
            if (markers[i].info.id >= 100 and markers[i].info.id <=123) or (markers[i].info.id  <= 53 and markers[i].info.id >= 50):  #if any arena tags or lair tags
                arena_update(markers[i].dist, markers[i].bearing.y, markers[i].rotation.x, markers[i].info.id)        
                print("arena tag found")

            #gems
            elif (markers[i].info.id <= 23 and markers[i].info.id >=20) or (markers[i].info.id <= 43 and markers[i].info.id >=40) :  #priority is gems: if we see the gem then we go for that first, because points and enemy cant get them
                #each gem will have two different codes
                if (markers[i].info.id == gem_id1) or (markers[i].info.id == gem_id2): #if it is our gem
                    gemlist.append(markers[i]) #record and add to list
                    print("our team gem found!")
                    #no need to sort these ones because we only have one unique team gem
                else:
                    othergemlist.append(markers[i]) #add to other list.

            elif markers[i].info.id <= 11:  #if they are sheep
                print("Sheep found")
                sheeplist.append(markers[i])
        gemlist = sort_markers(gemlist)
        othergemlist = sort_markers(othergemlist)
        sheeplist = sort_markers(sheeplist)

    if len(othergemlist) > 0:
        othergemlist.sort(key = attrgetter('dist'))  #sorts each list based on how far each marker is, from closest to furthest. 

    if len(sheeplist) > 0:
        sheeplist.sort(key = attrgetter('dist'))

    elif len(markers) == 1: #only 1 marker
        print("only 1 marker")
        if markers[0].info.id >= 100 and markers[0].info.id <=123:
            arena_update(markers[0].dist, markers[0].bearing.y, markers[0].rotation.x, markers[0].info.id)

            print("Arena tag found") #debug
            print(markers[0])

        elif markers[0].info.id == gem_id1:
            gemlist.append(markers[0])

            print("Our Gem found!")
            print(markers[0])

        elif markers[0].info.id <= 23 and markers[0].info.id >=20:
            othergemlist.append(markers[0])

            print("Other Team's gem found!")
            print(markers[0])

        elif markers[0].info.id <= 11:
            sheeplist.append(markers[0])

            print("Sheep found!")
            print(markers[0])
    # print(len(uniq_sheep))
    else:
        print("No gems found")
        pass

Lm_multi = 0.94
Rm_multi = 1
Velocity = 0.8

def move(distance, speed): # speed = 0-1
    time = distance / Velocity / speed
    time = time*(1+(1-speed)*0.15)
    print(time)
    r.motors[0] = 100 * Lm_multi * speed # Left motor
    r.motors[1] = 100 * Rm_multi * speed # Right motor 
    sleep(time)
    r.motors[0] = 0
    r.motors[1] = 0

    pos_update(distance,0)

spinr = 70
reductionInSpeed = 0.7

def spin(angle):
    spinT = (abs(angle)/spinr) + 0.05
    while angle > 360:
        angle -= 360
    if angle < 0:
        r.motors[0] = 5*Lm_multi*reductionInSpeed
        r.motors[1] = 80*Rm_multi*reductionInSpeed
    else:
        r.motors[0] = 50*Lm_multi*reductionInSpeed
        r.motors[1] = -75*Rm_multi*reductionInSpeed

    sleep(spinT)
    r.motors[0] = 0
    r.motors[1] = 0
    pos_update(0, angle)

TwistRateRight = 95
TwistRateLeft = 90
twist_multi = 0.5


def twist(angle): #twist 90 degrees to grab the box. 
    #positive angle: turn right
    #negative angle: turn left
    if angle < 0: 
        twistTRight = abs((angle/TwistRateRight)/twist_multi)
        print(twistTRight)
        r.motors[0] = 0
        r.motors[1] = 100*Rm_multi*twist_multi
        sleep(twistTRight)
        r.motors[0] = 0
        r.motors[1] = 0
    elif angle > 0: 
        twistTLeft = abs((angle/TwistRateLeft)/twist_multi)
        r.motors[0] = 100*Lm_multi*twist_multi  #need to tune for twisting left
        r.motors[1] = 0
        sleep(twistTLeft)
        r.motors[0] = 0
        r.motors[1] = 0
    else:
        pass
    pos_update(0, angle)






#movement macros

adj_times = 2

def move_to_gem(num):
    global adj_times
    spin(gemlist[num].bearing.y)
    move(gemlist[num].dist - 0.3, 0.7)  #start moving more accurately towards the gem: readjusting  

    for n in range(adj_times):
        analyse()
        spin(gemlist[num].bearing.y)
        move((gemlist[num].dist)/adj_times, 0.7)  #readjust. We may want to increase adj_times for more accuracy
        adj_times -= 1 #decrease adj_times
    
    adj_times = 2 #reset value

def move_to_other_gem(num):
    global adj_times
    spin(othergemlist[num].bearing.y)
    move(othergemlist[num].dist - 0.3, 0.7)  #start moving more accurately towards the gem: readjusting  

    for n in range(adj_times):
        analyse()
        spin(othergemlist[num].bearing.y)
        move((othergemlist)/adj_times, 0.7)  #readjust. We may want to increase adj_times for more accuracy
        adj_times -= 1 #decrease adj_times
    
    adj_times = 2 #reset value


def move_to_sheep(num):
    global adj_times
    spin(sheeplist[num].bearing.y)
    move(sheeplist[num].dist - 0.3, 0.7)

    for n in range(adj_times):
        analyse()
        spin(sheeplist[num].bearing.y)
        move((sheeplist[num].dist - 0.3)/adj_times, 0.7)
        adj_times -= 1 #decrease adj_times
    
    adj_times = 2 #reset value

def findangle(dx,dy):
    if dx>0:
        return math.degrees(math.atan(dy/dx))
    else:
        return 180 + math.degrees(math.atan(dy/dx))


lair_ref_pos = [0, 1.5, 270]

pos1 = [0, 0.5, 90]
pos2 = [-0.5, 1.5, 180]
pos3 = [0.5, 1.5, 0]


def move_next_pos(x,y):
    global selfpos
    d_x = x-selfpos[0]
    d_y = y-selfpos[1]

    print("d_x: ", d_x)
    print("d_y: ", d_y)

    if d_x != 0:
        d_angle = round(findangle(d_x,d_y) - selfpos[2], 4)
        
    else:
        d_angle = 0

    print("d_angle: ", d_angle)

    d_displacement = round(math.sqrt(d_x**2 + d_y**2), 4)
    print("d_displacement: ", d_displacement)

    spin(d_angle)
    move(d_displacement, 1)







#servo funcs and macros

def set_arm_servo_to_angle(angle):
#angle range = 1700 , 4600, 7500
#mid value 70
    value = 1700 + int(angle/180*5800)
    bus.write_byte_data(0x08, 1, value >> 8)
    bus.write_byte_data(0x08, 2, value & 0xff)

def set_claw_servo_to_angle(angle):
#angle range = 1700 , 4600, 7500
#mid value 70 
    value = 1700 + int(angle/180*5800)
    bus.write_byte_data(0x08, 3, value >> 8)
    bus.write_byte_data(0x08, 4, value & 0xff)
#arm funcs
def reset_arm_pos():
    set_arm_servo_to_angle(70)
def set_arm_to_store():
    set_arm_servo_to_angle(180)
def lower_arm():
    set_arm_servo_to_angle(0)
#claw funcs
def open_claw():
    set_claw_servo_to_angle(10)
def close_claw():
    set_claw_servo_to_angle(160)
#servo macros
def prepare_to_grab():
    open_claw()
    spin(gemlist[0].bearing.y)
    move(gemlist[0].dist)

def ir_check(sensorval):  #0 or 1; 0: front IR sensor; 1: claw IR sensor
    return r.gpio[sensorval].digital

def grab_box():
    twist(-90)
    lower_arm()
    sleep(1)
    close_claw()
    sleep(1)
    set_arm_to_store()
    sleep(2)
    open_claw()
    sleep(1)
    reset_arm_pos()

def game_setup():
    find_team_vals()
    reset_arm_pos()
    sleep(1)
    open_claw()
    sleep(1)
    rotate_tags(wall_tags, team)


scan_angle = 10 #degrees
def scan_pos():
    scan_times = 360/scan_angle #maybe only spin 90 degrees for each zone? otherwise if we spin 360 we will see boxes in other zones. 
    for i in range(scan_times):
        analyse()
        if len(gemlist) == 0:
            if len(othergemlist) == 0:
                if len(sheeplist) == 0:
                    pass
                else:
                    move_to_sheep(0)

            else:
                move_to_other_gem(0)
        else:
            move_to_gem(0)

def main():
    game_setup()
    move_next_pos(0, 0.5)
    analyse()
