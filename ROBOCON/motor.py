import robot
from time import sleep

r = robot.Robot()

t =   #Turn rate (degrees per second at full speed)
v =   #speed (centimeters per second at 90% speed)


def twist(angle): 
    
    twistT = abs(angle) / t  # Calculate time needed to twist

    #ADJUST THESE VALUES BELOW DEPENDING ON ACCURACY OF TURN
    if angle < 0:  # rotate left
        r.motors[0] = -15  # Left motor
        r.motors[1] = 15  # Right motor
    else:  # rotate right
        r.motors[0] = 15  # Left motor
        r.motors[1] = -15  # Right motor

    sleep(turnT)  
    r.motors[0] = 0  
    r.motors[1] = 0

def move(distance):
    
    moveT = distance / v  # Calculate time needed to move
    r.motors[0] = 90 
    r.motors[1] = 90
    sleep(moveT) 
    r.motors[0] = 0  
    r.motors[1] = 0





