import math
import robot # type: ignore
from time import sleep

r = robot.Robot()

t = 127   #Turn rate (degrees per second at full speed) 
twist_angle =  #Twist rate - speed robot turns 90 degrees with one motor stationary
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

gemlist = []
sheeplist = []
markers = []

def analyse():
    markers = r.see()
    #priority goes to gems first, then sheep. For each sheep, priority goes to the closest one it sees 
    def dist_calc(dist, rot, bear): #calculate dist. to centre of the box. Only requires one marker. 
        return (dist/math.sin(90 + rot)) * (math.sin(180- bear - rot))

    for marker in markers:

        if (markers[marker].info.id >= 100 and markers[marker].info.id <= 123) or (markers[marker].info.id >= 50 and markers[marker].info.id <= 53):  #if any arena tags or lair tags
            list.remove(markers[marker])
            pass #remove, we will deal with it in other functions.
        

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


pos1 = [0,1.5]
pos2 = [x2,y2]
pos3 = [x3,y3]
pos4 = [x4,y4]



selfpos = [None]*3

def rotate_tags(tags, zone):
    rotation = (zone + 2) % 4
    for i in range(rotation):
        for tag in tags:
            x = tag[0]
            y = tag[1]
            tag[0] = -1*y
            tag[1] = x
    for tag in tags:
        tag[2] -= 90*rotation
        tag[2] %= 360
        if tag[2] > 180:
            tag[2] -= 360

wall_tags = [
    [-250, 300, 0],
    [-150, 300, 0],
    [-50, 300, 0],
    [50, 300, 0],
    [150, 300, 0],
    [250, 300, 0],
    [300, 250, 90],
    [300, 150, 90],
    [300, 50, 90],
    [300, -50, 90],
    [300, -150, 90],
    [300, -250, 90],
    [250, -300, 180],
    [150, -300, 180],
    [50, -300, 180],
    [-50, -300, 180],
    [-150, -300, 180],
    [-250, -300, 180],
    [-300, -250, -90],
    [-300, -150, -90],
    [-300, -50, -90],
    [-300, 50, -90],
    [-300, 150, -90],
    [-300, 250, -90],
]

def compute_vector(dist, angle):
    if angle < 0:
        angle_to_x_axis = -90+angle
    else:
        angle_to_x_axis = 90-angle
    vector = [round(dist * math.cos(math.radians(angle_to_x_axis)), 5), round(dist * math.cos(math.radians(angle)), 5)]
    return vector

def pos_update(distance, bearing, rotation, id):
    id -= 100
    tag_normal = wall_tags[id][2]
    global selfpos
    selfpos[2] = tag_normal + rotation - bearing
    vector_to_tag = compute_vector(distance, tag_normal + bearing)
    #print(vector_to_tag)
    selfpos[0] = wall_tags[id][0] - vector_to_tag[0]
    selfpos[1] = wall_tags[id][1] - vector_to_tag[1]

#pos_update(2, 30, 60, 120)
#print(selfpos)
#rotate_tags(wall_tags, 3)
#print(wall_tags)

def check_wall():
    arenatags = r.see(lookfor= ARENA)

def grabbox():
   pass #lintian's later function
def releasebox():
   pass
def storebox():
   pass
def unloadbox():
   pass
def boxtolair():
   pass
def resetarmpos():
   pass

class main:
    def __init__(self, pos, dir, phase):
        self.pos = []
        self.dir = ()
        self.phase = 1
        #phase 1: start, move to pos1
        #phase 2: checking pos, rotating
        #phase 3: no markers seen, moving to next pos
        #phase 4: team gem seen, moving to gem, collecting gem
        #phase 5: box collected, move back to lair, deposit box, turn to face arena again
        #phase 6: sheep seen, moving to sheep, collecting sheep
        #phase 7: arena tag seen, update position

    def setphase(self, newphase):
        self.phase = newphase

    global spin_incr
    spin_incr = 20

    global spin_times
    spin_times = int(360/spin_incr)

    def checkboxes(self):
        for i in range(spin_times-1):
            analyse()
            spin(spin_incr)
            if len(uniq_gem) > 0:
                self.setphase(4)
                break
            elif len(uniq_sheep) > 0:
                self.setphase(6)
                break
        spin(20) # prevents us checking again when we have already checked all angles, but ensures we go back to our starting angle
        if len(uniq_gem) == 0 and len(uniq_sheep) == 0:
            self.setphase(3)


    def change_pos(posnum):
        #if no boxes are seen, or we just want to move to the next position, move there. 
        pass
    
    def movehome():
        pass
    

   
