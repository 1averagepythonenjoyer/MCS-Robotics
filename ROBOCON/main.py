import math
import robot # type: ignore
from time import sleep

r = robot.Robot()

#R.gpio[0].mode = robot.INPUT #IR sensor pin: add more later

selfpos = [0,-3,90]

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

wall_tags = [
    [-2.5, 3, 0],
    [-1.5, 3, 0],
    [-0.5, 3, 0],
    [0.5, 3, 0],
    [1.5, 3, 0],
    [2.5, 3, 0],
    [3, 2.5, 90],
    [3, 1.5, 90],
    [3, 0.5, 90],
    [3, -0.5, 90],
    [3, -1.5, 90],
    [3, -2.5, 90],
    [2.5, -3, 180],
    [1.5, -3, 180],
    [0.5, -3, 180],
    [-0.5, -3, 180],
    [-1.5, -3, 180],
    [-2.5, -3, 180],
    [-3, -2.5, -90],
    [-3, -1.5, -90],
    [-3, -0.5, -90],
    [-3, 0.5, -90],
    [-3, 1.5, -90],
    [-3, 2.5, -90],
]

def compute_vector(dist, angle):
    vector = [round(dist * math.cos(math.radians(angle)), 4), round(dist * math.cos(math.radians(90-angle)), 4)]
    return vector

def arena_update(distance, bearing, rotation, id):
    global selfpos
    id -= 100
    tag_normal = wall_tags[id][2]
    global selfpos
    selfpos[2] = tag_normal + rotation - bearing
    vector_to_tag = compute_vector(distance, tag_normal + bearing)
    #print(vector_to_tag)
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
gemlist = []
sheeplist = []

def analyse():
    markers = r.see()
    
    #priority goes to gems first, then sheep. For each sheep, priority goes to the closest one it sees 
    def dist_calc(dist, rot, bear): #calculate dist. to centre of the box. Only requires one marker. 
        return (dist/math.sin(90 + rot)) * (math.sin(180- bear - rot))

    for marker in markers:
        #arena tags
        if (markers[marker].info.id >= 100 and markers[marker].info.id <= 123) or (markers[marker].info.id >= 50 and markers[marker].info.id <= 53):  #if any arena tags or lair tags
            arena_update(markers[marker].distance, markers[marker].bearing, markers[marker].rotation, markers[marker].info.id)            
        
        if markers[marker].info.id == gem_value:  #priority is gems: if we see the gem then we go for that first, because points.
            gemlist.append(markers[marker])

############################################ check if gem is unique
        seen_gem = set()
        global uniq_gem
        uniq_gem = []
            
        for gem in gemlist:
            if marker[gem].info.id not in gemlist:  #if not, calculate the distance to the centre of the gem. 
                seen_gem.add(marker[gem])
                uniq_gem.append(marker[gem])
                marker[gem].distance = dist_calc(marker[gem].distance, marker[gem].rotation, marker[gem].bearing)   #calculate new distance to centre of the gem
                pass #add code to move to gem here.        
############################################
            else:
                if markers[marker].info.id >= 0 and markers[marker].info.id <= 19:
                    sheeplist.append(markers[marker])
                
                    seen_sheep = set()
                    global uniq_sheep
                    uniq_sheep = [] #final list of unique sheep boxes
                
                    for sheep in sheeplist:
                        if marker[sheep].info.id not in sheeplist:
                            seen_sheep.add(marker[sheep])
                            uniq_sheep.append(marker[sheep])
                            marker[sheep].distance = dist_calc(marker[sheep].distance, marker[sheep].rotation, marker[sheep].bearing)   #calculate new distance to centre of the box


#def check_wall():
#   arenatags = r.see(lookfor= ARENA)

t = 127   #Turn rate (degrees per second at full speed) 
#twist_angle =  #Twist rate - speed robot turns 90 degrees with one motor stationary
#^need value for this.
v = 0.321  #speed (centimeters per second)


multi_L = 1.0 #left motor multiplier from 0 to 1 not including 0 but include 1
if multi_L <= 0 or multi_L > 1:
    exit()

multi_R = 1.0 #right motor multiplier from 0 to 1 not including 0 but including 1
if multi_R <= 0 or multi_R > 1:
    exit()

def twist(angle): #Objective of 'twist' is for movement of robot to collect box and move with the box. 
                #If robot turns to sharply the box is not going to be with the robot
    while angle >= 360:
        angle -= 360  

    if angle == 0:
        exit()

    if angle > 0:
        r.motors[0] = 100 * multi_L
        r.motors[1] = 0
    else:
        r.motors[0] = 0
        r.motors[1] = 100 * multi_R

    sleep((angle / 90) * twist_angle)

def spin(angle): 
    
    spinT = (abs(angle) / t) + 0.05 # Calculate time needed to turn

    while angle > 360:
        angle -= 360

    if angle < 0:  # rotate left
        r.motors[0] =  100 * multi_L # Left motor
        r.motors[1] =  -100 * multi_R # Right motor
    else:  # rotate right
        r.motors[0] =  -100 * multi_L # Left motor
        r.motors[1] = 100 * multi_R  # Right motor

    sleep(spinT)  
    r.motors[0] = 0  
    r.motors[1] = 0
    pos_update(0, angle) #update orientation of robot 


def move(distance):
    if distance > 2:
        distance = 2
      
    if distance < -2:
        distance = -2  
      
    if distance == 0:
        exit()

    moveT = (distance / v) + 0.05  # Calculate time needed to move

    if distance > 0:
        r.motors[0] = 100 
        r.motors[1] = 100
    else:
        r.motors[0] = -100
        r.motors[1] = -100
       
    sleep(moveT) 
    r.motors[0] = 0  
    r.motors[1] = 0

    pos_update(distance, 0) #update position of the robot


default_positions = [[0, 1.5], [-1.5, 0], [1.5, 0], [0, -2]]

spin_incr = 20 #degrees
spin_times = int(360/spin_incr)

#scan each time you move 
adj_times = 2
def move_to_gem(num):
    analyse()
    spin(gemlist[num].bearing_y)
    move(gemlist[num].dist - 0.3)  #start moving more accurately towards the gem: readjusting  

    for n in range(adj_times):
        analyse()
        spin(gemlist[num].bearing_y)
        move((gemlist[num].dist - 0.3)/adj_times)  #readjust. We may want to increase adj_times for more accuracy

def move_to_sheep(num):
    analyse()
    spin(gemlist[num].bearing_y)
    move(gemlist[num].dist - 0.3)

    for n in range(adj_times):
        analyse()
        spin(gemlist[num].bearing_y)
        move((gemlist[num].dist - 0.3)/adj_times)

def main():
    move(2)
    move(2)
    move(0.5)
    sheep_or_gem = None
    for i in range(spin_times-1):
        analyse()
        if len(uniq_gem) == 0: #check if there any gems
            if len(uniq_sheep) ==0:  #if not, check for any sheep
                spin(spin_incr)

        elif len(uniq_gem) > 0: #if there is a gem, 
            sheep_or_gem = 0  #tell the robot that we are going for a gem
            break  # break out of the for() loop
        elif len(uniq_sheep) > 0: #if there is a sheep,
            sheep_or_gem = 1  #tell the robot that we are going for a sheep
            break  #break out of the for() loop
        spin(20) # prevents us checking again when we have already checked all angles, but ensures we go back to our starting angle
            
    if sheep_or_gem == 0:
        move_to_gem()
    elif sheep_or_gem == 1:
        move_to_sheep()

def findangle(dx,dy):
    if dx>0:
        return math.degrees(math.atan(dy/dx))
    else:
        return 180 + math.degrees(math.atan(dy/dx))

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
    pos_update(0, d_angle)
    pos_update(d_displacement, 0)
    
def test1():  #test function for 5th march 2025
    testpos = [[0, -1.5], [-1.5, 0]]
    #selfpos = [0,-3,0]  #x,y,orientation: what is orientation relative to though... we also need to define this relative to the camera of the brainbox 

    lairpos = [0,-3]

    move_next_pos(testpos[0][0], testpos[0][1]) #move to first test position
    
    print(selfpos) #should be [0,-1.5,0]

    move_next_pos(testpos[1][0], testpos[1][1])  #move to second test position
    
    print(selfpos)  #should be [-1.5,0]  

    move_next_pos(lairpos[0], lairpos[1])  #go back home
    print(selfpos)#

test1()
    
test1()
