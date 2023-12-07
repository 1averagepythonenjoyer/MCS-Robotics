import RPi.GPIO as GPIO
import time


Left_sensor_pin =      #modify for use!
middle_sensor_pin = 
right_senosr_pin =
left_motor_pin =
right_motor_pin  =

GPIO.setmode(GPIO.BCM)
GPIO.setup(left_sensor_pin, GPIO.IN)
GPIO.setup(middle_sensor_pin, GPIO.IN)
GPIO.setup(right_sensor_pin, GPIO.IN)
GPIO.setup(left_motor_pin, GPIO.OUT)
GPIO.setup(right_motor_pin, GPIO.OUT)

left_motor = GPIO.PWM(left_motor_pin, 100)
right_motor = GPIO.PWM(right_motor_pin, 100)
left_motor.start(0)
right_motor.start(0)

def forward(left_speed, right_speed):
    left_motor.ChangeDutyCycle(left_speed)
    right_motor.ChangeDutyCycle(right_speed)
    GPIO.output(left_motor_pin, GPIO.HIGH)
    GPIO.output(right_motor_pin, GPIO.HIGH)
def left():
    forward(100,70) #ADJUST!!!
def right():
    forwards(70,100) #ADJUST!!!
def stop():
    left_motor.ChangeDutyCycle(0)
    right_motor.ChangeDutyCycle(0)
    GPIO.output(left_motor_pin, GPIO.LOW)
    GPIO.output(right_motor_pin, GPIO.LOW)
def Lava_Palava():
    while True:
        left_sensor = GPIO.input(left_sensor_pin)
        middle_sensor = GPIO.input(middle_sensor_pin)
        right_sensor = GPIO.input(right_sensor_pin)
        
        if middle_sensor == 0:
            if left_sensor == 1:
                right()
            elif right_sensor == 1:
                left()
            else:
                stop()
        else:
            forward(100,100)
        time.sleep(0.1) #CUSTOMISE BUT I THINK 0.1 SECONDS PER CHECK IS GOOD ENOUGH

Lava_Palava()
