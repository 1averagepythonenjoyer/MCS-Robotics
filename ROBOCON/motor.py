import robot
from time import sleep

r = robot.Robot()

t =   #Turn rate (degrees per second at full speed)
v =   #speed (centimeters per second at 90% speed)

def turn(angle):
    if -3 <= angle <= 3:  # If the angle is within range, no need to turn
        return

    turnT = abs(angle) / t  # Calculate time needed to turn
    if angle < 0:  # Turn left
        r.motors[0] = 20  # Left motor
        r.motors[1] = -20  # Right motor
    else:  # Turn right
        r.motors[0] = 20  # Left motor
        r.motors[1] = -20  # Right motor

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





