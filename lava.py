import RPi.GPIO as GPIO
import time
from approxeng.input.selectbinder import ControllerResource

GPIO.setmode(BOARD)
GPIO.setwarnings(False)

left_sensor_pin = 29
middle_sensor_pin = 31
right_sensor_pin = 33

n = 0

GPIO.setup(left_sensor_pin, GPIO.IN)
GPIO.setup(middle_sensor_pin, GPIO.IN)
GPIO.setup(right_sensor_pin, GPIO.IN)

#motor enable pins
PWMaenable = 10
GPIO.setup(PWMaenable, GPIO.OUT)
PWMa= GPIO.PWM(PWMaenable, 100)
PWMa.start(0)

PWMbenable = 12
GPIO.setup(PWMbenable, GPIO.OUT)
PWMb= GPIO.PWM(PWMbenable, 100)
PWMb.start(0)

#motor pins
input1 = 21
GPIO.setup(input1, GPIO.OUT)
GPIO.output(input1, GPIO.HIGH)

input2 = 22
GPIO.setup(input2, GPIO.OUT)
GPIO.output(input2, GPIO.LOW)

input3 = 23
GPIO.setup(input1, GPIO.OUT)
GPIO.output(input1, GPIO.HIGH)

input4 = 24
GPIO.setup(input2, GPIO.OUT)
GPIO.output(input2, GPIO.LOW)

def start_mechanism():
    while joystick.connected: 
        joystick.check_presses()
        if joystick.presses.cross: #If active
            return True
        elif joystick.presses.triangle: # If not active
            time.sleep(0.1)
        elif joystick.presses.circle:
                GPIO.output(input1, GPIO.LOW)
                GPIO.output(input2, GPIO.LOW)
                GPIO.output(input3, GPIO.LOW)
                GPIO.output(input4, GPIO.LOW)
                quit()
    else:                        # Same as triangle but not sure what to even put here so I just put this
        time.sleep(0.1)

def Lava_Palava():
    while True:
        if start_mechanism() == True:
            global n
            if n == 3:
                GPIO.output(input1, GPIO.LOW)
                GPIO.output(input2, GPIO.LOW)
                GPIO.output(input3, GPIO.LOW)
                GPIO.output(input4, GPIO.LOW)
                quit()
            
            left_sensor = GPIO.input(left_sensor_pin)
            middle_sensor = GPIO.input(middle_sensor_pin)
            right_sensor = GPIO.input(right_sensor_pin)
            
            print("left =", left_sensor)  #Remember to remove this in the competition
            print("middle =", middle_sensor)
            print("right =", right_sensor)
            print("-------------------------------------------")
    
            start_mechanism():
            
            if left_sensor == 0 and middle_sensor == 0 and right_sensor == 0:
                start_mechanism():
                time.sleep(0.0001)
            elif left_sensor == 1 and middle_sensor == 1 and right_sensor == 0:
                start_mechanism():
                mix(0.99, 1)
                time.sleep(0.0001)
            elif right_sensor == 1  and middle_sensor == 1 and left_sensor == 0:
                start_mechanism(-0.99, 1):
                mix()
                time.sleep(0.0001)
            else:
                if middle_sensor == 0:
                    if left_sensor == 1 and right_sensor == 0:
                        start_mechanism():
                        mix(0.8, 1)
                        time.sleep(0.0001)
                        if left_sensor == 0 and right_sensor == 0:
                            start_mechanism():
                            mix(0.5, 1)
                            time.sleep(0.0001)
                    elif right_sensor == 1:
                        start_mechanism():
                        mix(-0.8,1)
                        time.sleep(0.0001)
                        if left_sensor == 0 and right_sensor == 0:
                            start_mechanism():
                            mix(-0.5, 100)
                            time.sleep(0.0001)
                else:
                    if middle_sensor == 1 and left_sensor == 0 and right_sensor == 0:
                        start_mechanism():
                        mix(0,100)
                        time.sleep(0.0001)
                    else:
                        if left_sensor == 0 and middle_sensor == 0 and right_sensor == 0:
                            start_mechanism():
                            mix(0,100)#We are at the finish. Robot needs to cross finish line to count as a win so we still have to forwards a bit
                            time.sleep(0.2)  #These add up to 1.2 seconds excluding sleeping extra 0.1 seconds. I did this so there would be as little delay for kill switch as possible. Maximum of 0.2 seconds delay is not too bad.
                            start_mechanism():
                            time.sleep(0.2)
                            start_mechanism():
                            time.sleep(0.2)
                            start_mechanism():
                            time.sleep(0.2)
                            start_mechanism():
                            time.sleep(0.2)
                            start_mechanism():
                            time.sleep(0.2)
                            start_mechanism():
                            time.sleep(0.1) #Sleep a little bit more so our hands can get off the robot
                            n+=1
                            break
                        start_mechanism():
                        break
                        
if left_sensor == 0 and right_sensor == 0 and middle_sensor == 1:
    start_mechanism():
    Lava_Palava()
    


