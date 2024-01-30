import RPi.GPIO as GPIO
import time
from approxeng.input.selectbinder import ControllerResource
from newmotor import * #this is a very useful function: imports all classes, variables, functions etc to your main file. 
#also means you don't have to do newmotor.(whateverfunction) every time


#CHANGE PINS ON PI
left_sensor_pin = 8 #Use Nicky's newmotor.py there is input 1-4 and that stuff in his code. I just need to set up sensors
middle_sensor_pin = 10
right_sensor_pin = 12

n = 0
presses = joystick.check_presses()

GPIO.setup(left_sensor_pin, GPIO.IN)
GPIO.setup(middle_sensor_pin, GPIO.IN)
GPIO.setup(right_sensor_pin, GPIO.IN)

def stop():
    global presses #VERY IMPORTNAT LOOK HERE!!!
    if '' in presses: #stop button whatever it may be
        mix(0,0)
        #quit()
        #exit()

def Lava_Palava():
    while True:
        global n
            
        left_sensor = GPIO.input(left_sensor_pin)
        middle_sensor = GPIO.input(middle_sensor_pin)
        right_sensor = GPIO.input(right_sensor_pin)
            
        print("left =", left_sensor)  #Remember to remove this in the competition
        print("middle =", middle_sensor)
        print("right =", right_sensor)
        print("-------------------------------------------")
    

            
        if left_sensor == 1 and middle_sensor == 1 and right_sensor == 0:
            mix(-0.95,1)
            time.sleep(0.0001)
            stop()
        elif right_sensor == 1  and middle_sensor == 1 and left_sensor == 0:
            mix(0.95,1)
            time.sleep(0.0001)
            stop()
        else:
            if middle_sensor == 0:
                if left_sensor == 1 and right_sensor == 0:
                    mix(-0.8,1)
                    time.sleep(0.0001)
                    stop()
                    if left_sensor == 0 and right_sensor == 0:
                        mix(-0.5,1)
                        time.sleep(0.0001)
                        stop()
                elif right_sensor == 1:
                    mix(0.8,1)
                    time.sleep(0.0001)
                    stop()
                    if left_sensor == 0 and right_sensor == 0:
                        mix(0.5,1)
                        time.sleep(0.0001)
                        stop()
            else:
                if middle_sensor == 1 and left_sensor == 0 and right_sensor == 0:
                    mix(0,1)
                    time.sleep(0.0001)
                    stop()
                else:
                    if left_sensor == 0 and middle_sensor == 0 and right_sensor == 0:
                        mix(0,1)
                        time.sleep(0.2)
                        stop()
                        time.sleep(0.2)
                        stop()
                        time.sleep(0.2)
                        stop()
                        time.sleep(0.2)
                        stop()
                        time.sleep(0.2)
                        stop()
                        time.sleep(0.2)
                        stop()
                        n+=1
                        mix(0,0)
                        break
                    break
                        
while n <= 3:
    global presses
    try:
        with ControllerResource() as joystick:
            while joystick.connected:
                if 'L1 Trigger' in presses: #start code button
                    if left_sensor == 1 or right_sensor == 1 or middle_sensor == 1:
                        time.sleep(0.1)
                        stop()
                        Lava_Palava()
    except IOError:
        #print('unable to find any joysticks')
        #Don't know what to put here
