import RPi.GPIO as GPIO
import time

GPIO.setmode(BOARD)
GPIO.setwarnings(False)

left_sensor_pin = 29
middle_sensor_pin = 31
right_sensor_pin = 33

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

def Lava_Palava():
    while True:
        global n
        if n == 3:
            quit()
        
        left_sensor = GPIO.input(left_sensor_pin)
        middle_sensor = GPIO.input(middle_sensor_pin)
        right_sensor = GPIO.input(right_sensor_pin)
        
        print("left =", left_sensor)  #Remember to remove this in the competition
        print("middle =", middle_sensor)
        print("right =", right_sensor)
        print("-------------------------------------------")
        
        if left_sensor == 0 and middle_sensor == 0 and right_sensor == 0:
            time.sleep(0.0001)
        elif left_sensor == 1 and middle_sensor == 1 and right_sensor == 0:
            mix()
            time.sleep(0.0001)
        elif right_sensor ==1  and middle_sensor == 1 and left_sensor == 0:
            mix()
            time.sleep(0.0001)
        else:
            if middle_sensor == 0:
                if left_sensor == 1 and right_sensor == 0:
                    mix()
                    time.sleep(0.0001)
                    if left_sensor == 0 and right_sensor == 0:
                        mix()
                        time.sleep(0.0001)
                elif right_sensor == 1:
                    mix()
                    time.sleep(0.0001)
                    if left_sensor == 0 and right_sensor == 0:
                        mix()
                        time.sleep(0.0001)
            else:
                if middle_sensor == 1 and left_sensor == 0 and right_sensor == 0:
                    mix()
                    time.sleep(0.0001)
                else:
                    if left_sensor == 0 and middle_sensor == 0 and right_sensor == 0:
                        mix()#We are at the finish. Robot needs to cross finish line to count as a win so we still have to forwards a bit
                        time.sleep(1.2) #Modify if you guys want but I don't think we need to
                            
                        time.sleep(0.01) #Sleep a little bit more so our hands can get off the robot
                        n+=1
                        break
                    break
                        
if left_sensor == 0 and right_sensor == 0 and middle_sensor == 1:
    Lava_Palava()
    


