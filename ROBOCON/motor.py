import robot
from time import sleep

r = robot.Robot()

t = 127   #Turn rate (degrees per second at full speed)  find this later 
v = 0.321  #speed (centimeters per second)


def twist(angle): 
    
    twistT = (abs(angle) / t) + 0.05 # Calculate time needed to twist

    while angle > 360:
        angle -= 360
    
    #ADJUST THESE VALUES BELOW DEPENDING ON ACCURACY OF TURN
    if angle < 0:  # rotate left
        r.motors[0] =  -100 # Left motor
        r.motors[1] =  100 # Right motor
    else:  # rotate right
        r.motors[0] =  100 # Left motor
        r.motors[1] = -100  # Right motor

    sleep(twistT)  
    r.motors[0] = 0  
    r.motors[1] = 0

def move(distance):
    if distance > 8:
        distance = 8
        
    if distance = 0:
        exit()

    moveT = (distance / v) + 0.05  # Calculate time needed to move

    if distance > 0:
        r.motors[0] = -100 
        r.motors[1] = - 90
        sleep(moveT) 
        r.motors[0] = 0  
        r.motors[1] = 0

    else:
        r.motors[0] = 100
        r.motors[1] = 90
        sleep(moveT) 
        r.motors[0] = 0  
        r.motors[1] = 0







