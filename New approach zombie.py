import cv2
from newmotor import *
import numpy as np
from RPiMotorLib import RPiservo
import RPi.GPIO as GPIO
import time
import model #Placeholder value for model. It will give us coordinates of target

GPIO.setmode(GPIO.BOARD)
GPIO.setwarnings(False)

camera_resolution = [x,y] #placeholder values! Please enetr integer but there is int() later just in case

solenoid_pin =
GPIO.setup(solenoid_pin, GPIO.OUT)

servo_pin =
servo = RPiservo()


servo_to_gun_ratio = a/b #placeholders! A is angle servo needs to turn, and B is the angle the gun actually turns



def model_work(numpy_array):
    model. #something here to make the model do some work
       

def capture_image():
    camera = cv2.VideoCapture(0)
    ret, frame = camera.read()
    if ret != True:
        print("Error! Frame cannot be captured!")
        quit()
    else:
        array = np.array()
           
    return model_work(array) #should return a coordinate variable name called 'target_coordinate'

current_angle = servo.read_servo_angle(servo_pin)

while True:
    try:
       
        middle_point_x = int(camera_resolution[0])/2
        middle_point_y = int(camera_resolution[-1])/2

        x_check_1 = target_coordinate[0]-2 < middle_point_x
        x_check_2 = middle_point < target_coordinate+2
        y_check_1 = target_coordinate[-1]-2 < middle_point_y
        y_check_2 = middle_point < target_coordinate+2
       
        if x_check_1 and x_check_2 and y_check_1 and y_check_2:
            GPIO.output(solenoid_pin)
            time.sleep(0.25) #Time Iden has given me
           
           
        elif middle_point_y - target_position[-1] < 5:
            servo.servo(np.prod([current_angle-=1, servo_to_gun_ratio]))
   
        elif target_position[-1] - middle_point_y < 5:
            servo.servo(np.prod([current_angle+=1], servo_to_gun_ratio))
         
        elif target_position[-1] < middle_point_y - target_coordinate[-1]/12:
            servo.servo(np.prod([current_angle-=5, servo_to_gun_ratio]))
           
        elif target_position[-1] > middle_point_y + target_coordinate[-1]/12:
            servo.servo(np.prod([current_angle+=5, servo_to_gun_ratio]))
           
        elif target_position[-1] < middle_point_y - target_coordinate[-1]/8:
            servo.servo(np.prod([current_angle-=10, servo_to_gun_ratio])) 
       
        elif target_position[-1] > middle_point_y + target_coordinate[-1]/8:
            servo.servo(np.prod([current_angle+=10, servo_to_gun_ratio]))
               
        elif target_position[-1] < middle_point_y - target_coordinate[-1]/4:
            servo.servo(np.prod(current_angle-=20, servo_to_gun_ratio))
             
        elif target_position[-1] > middle_point_y + target_coordinate[-1]/4: 
            servo.servo(np.prod(current_angle+=20, servo_to_gun_ratio))
           
           
        elif target_position[0] - middle_point_x < 5:
            mix(0, 0.1)

        elif middle_point_x - target_position[0] < 5:
            mix(0, -0.1)
           
        elif target_position[0] < middle_point_x  + 10:
            mix(0, 0.2)
            
        elif target_position[0] > middle_point_x - 10:
            mix(0, -0.2)
            
        elif target_position[0] < middle_point_x + 20:
            mix(0, 0.5)
            
        elif target_position[0] > middle_point_x - 20:
            mix(0, -0.5)
            
        elif target_position[0] > middle_point_x + 20:
            mix(0,1)
            
        elif target_position[0] < middle_point_x - 20:
            mix(0,-1)
            
        else:
            print("Something not in the logic/'unexpected occured!")
            quit()
     
       
    except KeyboardInterrupt:
        print("Program manually terminated through Keyboard Interrupt!")
        quit()
       
       
