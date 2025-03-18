import math
#from operator import attrgetter  #function which will fetch the attribute from each object
from time import sleep
import robot
import smbus2

bus = smbus2.SMBus(1)
r = robot.Robot(max_motor_voltage=12)

r.servos[0].mode = robot.PWM_SERVO 
r.servos[1].mode = robot.PWM_SERVO 


team = r.zone

# dictionary to store vals for each team
team_values = {
    robot.TEAM.RUBY: (20, 50),    # (gem_id, lairmarker_id)
    robot.TEAM.JADE: (21, 51),  #not sure if "robot.TEAM.RUBY" can be stored like this. 
    robot.TEAM.TOPAZ: (22, 52),
    robot.TEAM.DIAMOND: (23, 53),
}

def find_team_vals():
    global team, gem_id, lairmarker_id
    #gets values from dictionary
    gem_id, lairmarker_id = team_values.get(team, (None, None))  #still sets them to none if it doesn't fit the 

find_team_vals()

markers = []
gemlist = []  # most valuable. List of gem objects
othergemlist = [] # second most valuable
sheeplist = [] #third most valuable 

#coord_memory = [] #stores coordinates of any boxes we have seen

def dist_calc(dist, rot, bear): #calculate dist. to centre of the box. Only requires one marker. 
    return (dist/math.sin(90 + rot)) * (math.sin(90 - bear - rot))

#def coord_calc(dista, beari):
    pass

def analyse():
    markers_raw = r.see()
    for i in range(len(markers_raw)):
        markers.append(markers_raw[i-1])
        print(markers[i-1])

    
    #priority goes to gems first. For each sheep, priority goes to the closest one it sees 
    if len(markers) > 1: #only sort through the list if the list has more than 1 value. 
        print("more than 1 marker")
        for j in range(len(markers)):
            #arena tags
            if (markers[i-1].info.type == "MARKER_TYPE.ARENA") or (markers[i-1].info.id  <= 53 and markers[i-1].info.id >= 50):  #if any arena tags or lair tags
                arena_update(markers[i-1].dist, markers[i-1].bearing.y, markers[i-1].rotation.z, markers[i-1].info.id)        
        
            elif markers[i-1].info.id <= 23 and markers[i-1].info.id >=20:  #priority is gems: if we see the gem then we go for that first, because points and enemy cant get them
                #each gem will have two different codes
                if markers[i-1].info.id == gem_id: #if it is our gem
                    gemlist.append(markers[i-1]) #record and add to list
                    
############################################
                else:
                    othergemlist.append(markers[i-1]) #add to other list. 
                    seen_other_gem_ids = set()
                    uniq_other_gem = []

                    for othergem in gemlist:
                        othergem_id = othergem.info.id
                        if othergem_id not in seen_other_gem_ids: #if not, calculate the distance to the centre of the gem. 
                            seen_other_gem_ids.add(othergem_id)  # Mark this gem as seen, based on id
                            uniq_other_gem.append(othergem.info)
                            othergem.distance = dist_calc(othergem.dist, othergem.rotation.z, othergem.bearing.y)  # update distance


            elif marker.info.id <= 11:  #if they are sheep
                sheeplist.append(markers[i-1])
                
                seen_sheep_ids = set()
                uniq_sheep = [] #final list of unique sheep boxes
                
                for sheep in sheeplist:
                    sheep_id = sheep.info.id
                    if sheep.info.id not in sheeplist:
                        seen_sheep_ids.add(sheep_id)
                        uniq_sheep.append(sheep.info)
                        sheep.distance = dist_calc(sheep.dist, sheep.rotation.z, sheep.bearing.y)   #calculate new distance to centre of the box
    else:
        print("only 1 marker")
        if markers[0].info.type == "MARKER_TYPE.ARENA":
            arena_update(markers[0].dist, markers[0].bearing.y, marker.rotation.z, markers[0].info.id)
            print("Arena tag found")
        elif markers[0].info.id == gem_id:
            gemlist.append(markers[0])
            print("Our Gem found!")
        elif markers[0].info <= 23 and markers[0].info >=20:
            othergemlist.append(markers[0])
            print("Other Team's gem found!")
        elif markers[0].info.id <= 11:
            sheeplist.append(markers[0])
            print("Sheep found!")


    # if len(uniq_other_gem) > 0:
    #     uniq_other_gem.sort(key = attrgetter('dist'))  #sorts each list based on how far each marker is, from closest to furthest. 
    # if len(uniq_sheep) > 0:
    #     uniq_sheep.sort(key = attrgetter('dist'))

Lm_multi = 0.956
Rm_multi = 1
Velocity = 0.97

def move(distance, speed): # speed = 0-1
    time = distance / Velocity / speed
    time = time*(1+(1-speed)*0.15)
    r.motors[0] = 100 * Lm_multi * speed # Left motor
    r.motors[1] = 100 * Rm_multi * speed # Right motor 
    sleep(time)
    r.motors[0] = 0
    r.motors[1] = 0

t = 221
reductionInSpeed = 0.7

def spin(angle):
    spinT = (abs(angle)/t) + 0.05
    while angle > 360:
        angle -= 360
    if angle < 0:
        r.motors[0] = 100*Lm_multi*reductionInSpeed
        r.motors[1] = 100*Rm_multi*reductionInSpeed
    else:
        r.motors[0] = -100*Lm_multi*reductionInSpeed
        r.motors[1] = 100*Rm_multi*reductionInSpeed

    sleep(spinT)
    r.motors[0] = 0
    r.motors[1] = 0

TwistRateRight = 142
TwistRateLeft = 147
twist_multi = 0.5


def twist(angle): #twist 90 degrees to grab the box. 
    #positive angle: turn right
    #negative angle: turn left
    if angle < 0: 
        twistTRight = abs((angle/TwistRateRight)/twist_multi)
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



analyse()
if len(gemlist) !=0:

    set_arm_servo_to_angle(70)
    sleep(2)
    twist(gemlist[0].bearing.y)
    sleep(1)
    move(gemlist[0].dist-0.05, 0.5)
    sleep(2)
    twist(-90)
    sleep(1)
    set_arm_servo_to_angle(0)
    sleep(2)
    set_claw_servo_to_angle(160)
    sleep(2)
    set_arm_servo_to_angle(180)
    sleep(3)
    set_claw_servo_to_angle(10)
    sleep(1)
    set_arm_servo_to_angle(70)
    print("yay")



# markers = r.see()
# newmarker = []
# for i in range(len(markers)):
#     newmarker.append(markers[i-1])
#     print(newmarker[i-1].bearing.y)

