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
selfpos = [0.7,-2.65,90]  #does this need to be the camera, or the middle of the robot?

#get team name
# dictionary to store vals for each team
if r.zone == robot.TEAM.RUBY:
    team_value = (20, 40, 50, 0)
elif r.zone == robot.TEAM.TOPAZ:
    team_value= (21, 41, 51, 1)   # (gem_id1, lairmarker_id)
elif r.zone == robot.TEAM.JADE:
    team_value= (22, 42, 52, 2)  # (gem_id1, lairmarker_id)
elif r.zone == robot.TEAM.DIAMOND:
    team_value= (23, 43, 53, 3)   # (gem_id1, lairmarker_id)

def find_team_vals():
    global team, gem_id1, gem_id2, lairmarker_id
    #gets values from dictionary
    gem_id1 = team_value[0]
    gem_id2 = team_value[1]
    lairmarker_id = team_value[2]
    team = team_value[3]

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
    selfpos[2] = tag_normal + bearing - rotation
    selfpos[2] %= 360
    vector_to_tag = compute_vector(distance, tag_normal - rotation)
    selfpos[0] = wall_tags[id][0] - vector_to_tag[0]
    selfpos[1] = wall_tags[id][1] - vector_to_tag[1]
    print("updated from arena tag: ", selfpos)

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

# def dist_calc(dist, rot, bear): #calculate dist. to centre of the box. Only requires one marker. 
#     return (dist/math.sin(90 + rot)) * (math.sin(90 - bear - rot))

def sort_markers(markers): #check if this works....
    #removes duplicates
    seen_ids = set()
    uniq_markers = []   #final list of unique markers   
    for marker in markers:
        marker_id = marker.info.id
        if marker_id not in seen_ids:
            seen_ids.add(marker_id)
            uniq_markers.append(marker)
            # marker.dist = dist_calc(marker.dist, marker.rotation.x, marker.bearing.y)  #calculate new distance to centre
    return uniq_markers
markers = []
gemlist = []  # most valuable. List of gem objects
othergemlist = [] # second most valuable
sheeplist = [] #third most valuable 


def analyse():
    global markers
    global gemlist
    global othergemlist
    global sheeplist
    markers = r.see()
    #priority goes to gems first. For each sheep, priority goes to the closest one it sees 
    if len(markers) > 1: #only sort through the list if the list has more than 1 value. 
        print("more than 1 marker")
        for i in range(len(markers)):
            # print(markers[i]) #debug

            #arena tags
            if (markers[i].info.id >= 100 and markers[i].info.id <=123):
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
            # print(markers[0])

        elif markers[0].info.id == gem_id1:
            gemlist.append(markers[0])

            print("Our Gem found!")
            # print(markers[0])

        elif markers[0].info.id <= 23 and markers[0].info.id >=20:
            othergemlist.append(markers[0])

            print("Other Team's gem found!")
            # print(markers[0])

        elif markers[0].info.id <= 11:
            sheeplist.append(markers[0])

            print("Sheep found!")
            # print(markers[0])
    # print(len(uniq_sheep))
    else:
        print("No gems found")
        pass

Lm_multi = 0.95
Rm_multi = 1
Lm_multi_speed = 0.96
Rm_multi_speed = 0.95
Velocity = 0.88

def move(distance, speed): # speed = 0-1
    time = distance / Velocity / speed
    time *= (1+(1-speed)*0.15)
    r.motors[0] = 100 * Lm_multi * speed * Lm_multi_speed # Left motor
    r.motors[1] = 100 * Rm_multi * speed * Rm_multi_speed# Right motor 
    sleep(abs(time))
    r.motors[0] = 0
    r.motors[1] = 0

    pos_update(distance,0)
    print("current pos: ", selfpos)

LEFTspinr = 65
RIGHTspinr = 69
reductionInSpeed = 0.56


def spin(angle):
    LEFTspinT = (abs(angle)/LEFTspinr) + 0.05
    RIGHTspinT = (abs(angle)/RIGHTspinr) + 0.05

    while angle > 360:
        angle -= 360
    if angle < 0:
        r.motors[0] = -50*Lm_multi*reductionInSpeed
        r.motors[1] = 80*Rm_multi*reductionInSpeed
        sleep(LEFTspinT)
        r.motors[0] = 0
        r.motors[1] = 0
        pos_update(0, angle)
    else:
        r.motors[0] = 70*Lm_multi*reductionInSpeed
        r.motors[1] = -63*Rm_multi*reductionInSpeed
        sleep(RIGHTspinT)
        r.motors[0] = 0
        r.motors[1] = 0
        pos_update(0, -1*angle)

    

TwistRateRight = 110
TwistRateLeft = 90
Rm_twist_multi = 0.55
Lm_twist_multi = 0.49

def twist(angle): #twist 90 degrees to grab the box. 
    #positive angle: turn right
    #negative angle: turn left
    if angle < 0: 
        twistTRight = abs((angle/TwistRateRight)/Rm_twist_multi)
        r.motors[0] = 0
        r.motors[1] = 100*Rm_multi*Rm_twist_multi
        sleep(twistTRight)
        r.motors[0] = 0
        r.motors[1] = 0
    elif angle > 0: 
        twistTLeft = abs((angle/TwistRateLeft)/Lm_twist_multi)
        r.motors[0] = 100*Lm_multi*Lm_twist_multi  #need to tune for twisting left
        r.motors[1] = 0
        sleep(twistTLeft)
        r.motors[0] = 0
        r.motors[1] = 0
    else:
        pass
    pos_update(0, angle)
    print("selfpos after twisting: ", selfpos)






#movement macros

def move_to_gem(num):
    spin(gemlist[num].bearing.y)
    firstmove = gemlist[num].dist
    print("move to gem dist:", firstmove)
    move(firstmove, 0.5)  #start moving more accurately towards the gem: readjusting

def move_to_other_gem(num):
    spin(othergemlist[num].bearing.y)
    firstmove = othergemlist[num].dist
    print("move to othergem dist: ", firstmove)
    move(firstmove, 0.5)  #start moving more accurately towards the gem: readjusting
    

def move_to_sheep(num):
    spin(sheeplist[num].bearing.y)
    firstmove = sheeplist[num].dist
    print("move to sheep dist: ", firstmove)
    move(firstmove, 0.5)  #start moving more accurately towards the gem: readjusting  

def findangle(dx,dy):
    if dx>0:
        return math.degrees(math.atan(dy/dx))
    else:
        return 180 + math.degrees(math.atan(dy/dx))


lair_ref_pos = [0, -2, 270]

pos1 = [0.7, 1, 90]
pos2 = [-1.5, 1, 180]
# pos3 = [0.5, 1.5, 0]


def move_next_pos(x,y):
    global selfpos
    d_x = x-selfpos[0]
    d_y = y-selfpos[1]

    print("d_x: ", d_x)
    print("d_y: ", d_y)

    if d_x != 0:
        d_angle = round(selfpos[2] - findangle(d_x,d_y), 4)
        
    else: #prevents division by zero
        d_angle = 0

    print("d_angle: ", d_angle)

    d_displacement = round(math.sqrt(d_x**2 + d_y**2), 4)
    print("d_displacement: ", d_displacement)
    print("moving to next position, distance, ", d_displacement)
    spin(d_angle)
    move(d_displacement, 0.7)


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
    set_claw_servo_to_angle(0)
def close_claw():
    set_claw_servo_to_angle(180)
#servo macros
def prepare_to_grab():
    open_claw()
    spin(gemlist[0].bearing.y)
    move(gemlist[0].dist)

def ir_check(sensorval):  #0 or 1; 0: front IR sensor; 1: claw IR sensor
    return r.gpio[sensorval].digital


def grab_box(): 
    twist(-180)
    print("twisting")
    lower_arm()
    sleep(1)
    close_claw()
    sleep(1)
    set_arm_to_store()
    sleep(2)
    open_claw()
    sleep(1)
    reset_arm_pos()
    sleep(1)
    


def game_setup():
    find_team_vals()
    lower_arm()
    sleep(1)
    rotate_tags(wall_tags, team)


scan_angle = 10 #degrees
def scan_pos():
    global markers 
    global gemlist  
    global othergemlist 
    global sheeplist 
    scan_times = int(360/scan_angle) #maybe only spin 90 degrees for each zone? otherwise if we spin 360 we will see boxes in other zones. 
    for i in range(scan_times):
        analyse()
        if len(gemlist) == 0:
            if len(othergemlist) == 0:
                if len(sheeplist) == 0:
                    spin(-scan_angle)
                    sleep(1)
                else:
                    move_to_sheep(0)
                    print("moving to sheep")
                    sleep(1)
                    grab_box()
                    markers = []
                    gemlist = []  # most valuable. List of gem objects
                    othergemlist = [] # second most valuable
                    sheeplist = [] #third most valuable 
                    break

            else:
                move_to_other_gem(0)
                print("going to gem")
                grab_box()
                markers = []
                gemlist = []  # most valuable. List of gem objects
                othergemlist = [] # second most valuable
                sheeplist = [] #third most valuable 
                break
        else:
            move_to_gem(0)
            grab_box()
            markers = []
            gemlist = []  # most valuable. List of gem objects
            othergemlist = [] # second most valuable
            sheeplist = [] #third most valuable 
            break




def main():
    reset_arm_pos()
    sleep(0.5)
    open_claw()
    move_next_pos(pos1[0], pos1[1])
    irsensor = not ir_check(0)
    if irsensor == True:
        grab_box()
    scan_pos()
    
    
        
game_setup()
while True:    
    main()




# move(3.5,0.7)

# while True:
#     sleep(1)
#     irsensor = not ir_check(0)
#     print(irsensor)
    

# move(1,0.7)
# twist(-90)
# open_claw()
# spin(360)

