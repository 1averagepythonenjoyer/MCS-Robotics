import RPi.GPIO as GPIO
import time

GPIO.setmode(BOARD)
GPIO.setwarnings(False)

leftsensor = 29
middlesensor = 31
rightsensor = 33

GPIO.setup(leftsensor, GPIO.IN)
GPIO.setup(middlesensor, GPIO.IN)
GPIO.setup(rightsensor, GPIO.IN)

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

def forward(left_speed, right_speed):
    PWMb.ChangeDutyCycle(left_speed)
    PWMa.ChangeDutyCycle(right_speed)
def left():
    forward(70,100)
def right():
    forward(100,70) 
def left_a_bit():
    forward(90,100) 
def right_a_bit():
    forward(100,90) 
def ultimate_left(): 
    forward(20,100)
def ultimate_right():
    forward(100,20) 
def Lava_Palava():
    while True:
        global n
        if n == 3:
            quit()
        
        left_sensor = GPIO.input(leftsensor)
        middle_sensor = GPIO.input(middlesensor)
        right_sensor = GPIO.input(rightsensor)
        
        print("left =", left_sensor)
        print("middle =", middle_sensor)
        print("right =", right_sensor)
        print("-------------------------------------------")
        
        if left_sensor and middle_sensor and right_sensor == 0:
            sleep(0.0001)
        elif left_sensor and middle_sensor == 1:
            left_a_bit()
            time.sleep(0.0001)
        elif right_sensor and middle_sensor == 1:
            right_a_bit()
            time.sleep(0.0001)
        else:
            if middle_sensor == 0:
                if left_sensor == 1:
                    left()
                    time.sleep(0.0001)
                    if left_sensor and right_sensor == 0:
                        ultimate_left()
                elif right_sensor == 1:
                    right()
                    time.sleep(0.0001)
                    if left_sensor and right_sensor == 0:
                        ultimate_right()
            else:
                if middle_sensor == 1 and left_sensor and right_sensor == 0:
                    forward(100,100)
                    time.sleep(0.0001)
                else:
                    if n != 0:
                        while left_sensor and middle_sensor and right_sensor == 0:
                            forward(100,100) #We are at the finish. Robot needs to cross finish line to count as a win so we still have to forwards a bit
                            time.sleep(0.01) #Modify if you guys want but I don't think we need to
                        time.sleep(0.1) #Sleep a little bit more so our hands can get off the robot
                        n+=1
                        break
                        
if left_sensor and right_sensor == 0 and middle_sensor == 1:
    time.sleep(0.1)
    Lava_Palava()
    


