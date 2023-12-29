
import RPi.GPIO as GPIO
import time


left_sensor_pin =  29    #modify for use!
middle_sensor_pin = 31#We can just glue three jumper wires together.
right_sensor_pin = 33

rightinput_1 = 21  #same as the ones for the remote control: this makes it less confusing. It also allows us to manually override the robot 
rightinput_2 = 22
leftinput_1 = 23
leftinput_2 = 24
GPIO.output(rightinput_1, HIGH)
GPIO.output(rightinput_2, LOW)# if something doesnt work
GPIO.output(leftinput_1, HIGH)
GPIO.output(leftinput_1, LOW)

n = 0 #DO NOT CHANGE!

GPIO.setwarnings(False)

GPIO.setmode(GPIO.BOARD)
GPIO.setup(left_sensor_pin, GPIO.IN)
GPIO.setup(middle_sensor_pin, GPIO.IN)
GPIO.setup(right_sensor_pin, GPIO.IN)


left_motor = GPIO.PWM(leftinput_1, 100)
right_motor = GPIO.PWM(rightinput_1, 100)
left_motor.start(0)
right_motor.start(0)

def forward(left_speed, right_speed):
    left_motor.ChangeDutyCycle(left_speed)
    right_motor.ChangeDutyCycle(right_speed)
def left():
    forward(70,100) #ADJUST!!!
def right():
    forwards(100,70) #ADJUST!!!
def left_a_bit():
    forward(90,100) #ADJUST!!!
def right_a_bit():
    forward(100,90) #ADJUST
def ultimate_left(): 
    forward(20,100) #ADJUST!!!
def ultimate_right():
    forward(100,20) #ADJUST!!!
def Lava_Palava():
    while True:
        left_sensor = GPIO.input(left_sensor_pin)
        middle_sensor = GPIO.input(middle_sensor_pin)
        right_sensor = GPIO.input(right_sensor_pin)
        
        if left_sensor and middle_sensor == 1:
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
                    break
Lava_Palava()


