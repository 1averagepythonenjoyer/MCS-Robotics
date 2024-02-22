try:    
    import RPi.GPIO
    from practicemotors import *
    import time

    GPIO.setmode(GPIO.BOARD)
    GPIO.setwarnings(False)

    left_sensor_pin = 11
    middle_sensor_pin = 10
    right_sensor_pin = 12

    GPIO.setup(left_sensor_pin, GPIO.IN)
    GPIO.setup(middle_sensor_pin, GPIO.IN)
    GPIO.setup(right_sensor_pin, GPIO.IN)

    speed_factor = 
    if speed_factor > 1 or speed_factor < 0:
        print('Please give a speed_factor of value between 0 and 1 only!')
        quit()
    
    def go(left_speed, right_speed):
        try:
            set(left_speed*speed_factor, speed_factor*(right_speed-1.5))
            time.sleep(0.0001)
        except KeyboardInterrupt:
            print("Keyboard Interrupt while forwards")
            quit()

    def check():
        global left_sensor, middle_sensor, right_sensor
        left_sensor = GPIO.input(left_sensor_pin)
        middle_sensor = GPIO.input(middle_sensor_pin)
        right_sensor = GPIO.input(right_sensor_pin)

    while True:
        try:   
            plus_speed = 0
            
            check()
                
            if left_sensor == 0 and right_sensor == 0 and middle_sensor ==1:
                go(30*,30)
                    
            elif left_sensor == 1 and middle_sensor == 1 and right_sensor == 0:
                go(30,40)
                
            elif right_sensor == 1 and middle_sensor == 1 and left_sensor == 0:
                go(40,30)

            else:
                if middle_sensor == 0 and left_sensor == 0 and right_sensor == 1:
                    go(50,30)
                    check()
                    while middle_sensor == 0 and left_sensor == 0 and right_sensor == 0:
                        go(60,10)
                        check()
                                
                elif middle_sensor == 0 and right_sensor== 0 and left_sensor == 1:
                    go(30,50)
                    check()
                    
                    while middle_sensor == 0 and right_sensor== 0 and left_sensor == 0:
                        go(10,60+plus_speed)
                        plus_speed+=2
                        check()
                        
                else: #no sensors
                    if left_sensor == 0 and right_sensor == 0 and middle_sensor == 0:
                        set(30*speed_factor,29*speed_factor)
                        time.sleep(2)
                        check()
                        if left_sensor == 0 and right_sensor == 0 and middle_sensor == 0:
                            set(0,0)
                            time.sleep(1)
                            check()
                            while left_sensor == 0 and right_sensor == 0 and middle_sensor == 0:
                                check()
                                time.sleep(0.01)
                            
                                
        except KeyboardInterrupt:
            print("Manual stop on computer")
            quit()
        
except ModuleNotFoundError:
    print('Library not installed and/or not installed correctly!')
    quit()
