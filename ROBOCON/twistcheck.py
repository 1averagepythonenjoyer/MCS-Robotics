def twist(angle): #Objective of 'twist' is for movement of robot to collect box and move with the box. 
                #If robot turns to sharply the box is not going to be with the robot
    while angle >= 360:
        angle -= 360  

    if angle == 0:
        exit()

    if angle > 0:
        r.motors[0] = 100 * LM_multi
        r.motors[1] = 0
    else:
        r.motors[0] = 0
        r.motors[1] = 100 * RM_multi

    sleep((angle / 90) * twist_angle)

    
