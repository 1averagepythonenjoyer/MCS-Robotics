import robot
from time import sleep 
r = robot.Robot(max_motor_voltage=12)
Lm_multi = 0.956
Rm_multi = 1
Velocity = 0.97


def move(distance, speed): # speed = 0-1
    r.motors[0] = 100 * Lm_multi * speed  # Left motor
    r.motors[1] = 100 * Rm_multi * speed # Right motor 
    time = distance / Velocity / speed
    time = time*(1+(1-speed)*0.15)
    sleep(time) 
    r.motors[0] = 0  
    r.motors[1] = 0



move(1, 0.5)
