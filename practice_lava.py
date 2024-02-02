import RPi.GPIO as GPIO
import time
from practicemotors import *

#customize
left_sensor_pin = 
middle_sensor_pin = 
right_sensor_pin = 

n = 0

GPIO.setup(left_sensor_pin, GPIO.IN)
GPIO.setup(middle_sensor_pin, GPIO.IN)
GPIO.setup(right_sensor_pin, GPIO.IN)

def Lava_Palava():
    while True:
            
        left_sensor = GPIO.input(left_sensor_pin)
        middle_sensor = GPIO.input(middle_sensor_pin)
        right_sensor = GPIO.input(right_sensor_pin)
            
        print("left =", left_sensor)  #Remember to remove this in the competition
        print("middle =", middle_sensor)
        print("right =", right_sensor)
        print("-------------------------------------------")
    

            
        if left_sensor == 1 and middle_sensor == 1 and right_sensor == 0:
            set(95,100)
            time.sleep(0.0001)
        elif right_sensor == 1  and middle_sensor == 1 and left_sensor == 0:
            set(100,95)
            time.sleep(0.0001)
        else:
            if middle_sensor == 0:
                if left_sensor == 1 and right_sensor == 0:
                    set(80,100)
                    time.sleep(0.0001)
                    if left_sensor == 0 and right_sensor == 0:
                        set(50,100)
                        time.sleep(0.0001)
                elif right_sensor == 1:
                    set(100,80)
                    time.sleep(0.0001)
                    if left_sensor == 0 and right_sensor == 0:
                        set(100,50)
                        time.sleep(0.0001)
            else:
                if middle_sensor == 1 and left_sensor == 0 and right_sensor == 0:
                    set(100,100)
                    time.sleep(0.0001)
                else:
                    if left_sensor == 0 and middle_sensor == 0 and right_sensor == 0:
                        set(100,100)
                        time.sleep(1.2)
                        n+=1
                        set(0,0)
                        break
                    break
while n <= 3:                        
    if left_sensor == 1 or right_sensor == 1 or middle_sensor == 1 and n <= 3:
        time.sleep(0.1)
        Lava_Palava()
GPIO.cleanup()
