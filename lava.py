try:   #Right now, the deal is is that the motors are to slow moving to do +amount and -amount. Later just remove the -amount 
    import RPi.GPIO as GPIO
    from mcsmotors import *

    GPIO.setmode(GPIO.BOARD)
    GPIO.setwarnings(False)

    left_sensor_pin = 11
    middle_sensor_pin = 10
    right_sensor_pin = 12

    GPIO.setup(left_sensor_pin, GPIO.IN)
    GPIO.setup(middle_sensor_pin, GPIO.IN)
    GPIO.setup(right_sensor_pin, GPIO.IN)
    
    speed_factor = 1#Please enter a number from 0 to 1 inclusive
    if speed_factor < 0 or speed_factor > 1:
        print("Please give a speed factor of between 0 and 1")
        quit()

    def forward(yaw, throttle, speed_factor):
        try:
            yawthrottle(yaw, throttle, speed_factor)
        except KeyboardInterrupt:
            print('manual stop while moving')    
            quit()
        
    def check():
        global left_sensor, middle_sensor, right_sensor
        left_sensor = GPIO.input(left_sensor_pin)
        middle_sensor = GPIO.input(middle_sensor_pin)
        right_sensor = GPIO.input(right_sensor_pin)

    while True:
        try:   
            amount = 0
            
            check()
                
            if left_sensor == 0 and right_sensor == 0 and middle_sensor ==1:
                forward(0, 0.3, speed_factor)
                    
            elif left_sensor == 1 and middle_sensor == 1 and right_sensor == 0:
                forward(-0.15, 0.3, speed_factor)
                
            elif right_sensor == 1 and middle_sensor == 1 and left_sensor == 0:
                forward(0.15, 0.3, speed_factor)

            else:
                if middle_sensor == 0 and left_sensor == 0 and right_sensor == 1:
                    forward(0.35, 0.3, speed_factor)
                    check()
                    while middle_sensor == 0 and left_sensor == 0 and right_sensor == 0:
                        forward(0.7+amount, 0.3, speed_factor)
                        amount+=2
                        check()
                                
                elif middle_sensor == 0 and right_sensor== 0 and left_sensor == 1:
                    forward(-0.35, 0.3, speed_factor)
                    check()
                    
                    while middle_sensor == 0 and right_sensor== 0 and left_sensor == 0:
                        forward(-0.7-amount, 0.3, speed_factor)
                        amount+=2
                        check()
                        
                else: #no sensors
                    if left_sensor == 0 and right_sensor == 0 and middle_sensor == 0:
                        forward(0, 0.3, speed_factor)
                        
                                
        except KeyboardInterrupt:
            print("Manual stop on computer")
            quit()
        
except ModuleNotFoundError:
    print('Library not installed and/or not installed correctly!')
    quit()
