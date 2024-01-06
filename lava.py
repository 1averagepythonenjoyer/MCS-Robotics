import RPi.GPIO as GPIO
import time

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)

left_sensor_pin =  29    
middle_sensor_pin = 31 #We can just glue three jumper wires together.
right_sensor_pin = 33

GPIO.setup(left_sensor_pin, GPIO.IN)
GPIO.setup(middle_sensor_pin, GPIO.IN)
GPIO.setup(right_sensor_pin, GPIO.IN)

n = 0

right_motor_input_1 = 21  #same as the ones for the remote control: this makes it less confusing. It also allows us to manually override the robot 
right_motor_input_2 = 22
left_motor_input_1 = 23
left_motor_input_2 = 24

GPIO.setup(right_motor_input_1,GPIO.OUT)
GPIO.setup(right_motor_input_2,GPIO.OUT)
GPIO.setup(left_motor_input_1,GPIO.OUT)
GPIO.setup(left_motor_input_2,GPIO.OUT)

GPIO.output(right_motor_input_1, GPIO.HIGH)
GPIO.output(right_motor_input_2, GPIO.LOW)# if something doesnt work
GPIO.output(left_motor_input_1, GPIO.HIGH)
GPIO.output(left_motor_input_1, GPIO.LOW)

left_motor = GPIO.PWM(left_motor_input_1, 100)
right_motor = GPIO.PWM(right_motor_input_1, 100)
right_motor.start(0)
left_motor.start(0)

def forward(left_speed, right_speed):
    left_motor.ChangeDutyCycle(left_speed)
    right_motor.ChangeDutyCycle(right_speed)
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
        left_sensor = GPIO.input(left_sensor_pin)
        middle_sensor = GPIO.input(middle_sensor_pin)
        right_sensor = GPIO.input(right_sensor_pin)
        
        if n == 0:
            n += 1
        
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
                    if n < 1:
                        while left_sensor and middle_sensor and right_sensor == 0:
                            forward(100,100) #We are at the finish. Robot needs to cross finish line to count as a win so we still have to forwards a bit
                            time.sleep(0.01) #Modify if you guys want but I don't think we need to
                        time.sleep(0.1) #Sleep a little bit more so our hands can get off the robot
                    break
Lava_Palava()
