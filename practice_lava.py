try:    
    import RPi.GPIO
    from practicemotors import *
    from time import sleep

    GPIO.setmode(GPIO.BOARD)
    GPIO.setwarnings(False)

    left_sensor_pin = 11
    middle_sensor_pin = 10
    right_sensor_pin = 12

    GPIO.setup(left_sensor_pin, GPIO.IN)
    GPIO.setup(middle_sensor_pin, GPIO.IN)
    GPIO.setup(right_sensor_pin, GPIO.IN)

    def go(left_speed, right_speed):
        try:
            set(50,0)
            print('step after 0,50 set reached')
            sleep(0.102)
            set(left_speed+2, right_speed)
            print('step after left_speed right_speed-1 reached')
            sleep(10)
            set(0,0)
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
            n = 0
                
            check()
                
            if left_sensor == 0 and right_sensor == 0 and middle_sensor ==1:
                go(30,30)
                sleep(0.0001)
                    
            elif left_sensor == 1 and middle_sensor == 1 and right_sensor == 0:
                go(0,0)
                sleep(0.0001)
                
            elif right_sensor == 1 and middle_sensor == 1 and left_sensor == 0:
                go(15,11)
                sleep(0.0001)

            else:
                if middle_sensor == 0 and left_sensor == 0 and right_sensor == 1:
                    go(30,11)
                    sleep(0.0001)
                    check()
                    
                    if middle_sensor == 0 and left_sensor == 0 and right_sensor == 0:
                        go(50,11)
                        sleep(0.0001)
                            
                elif middle_sensor == 0 and right_sensor== 0 and left_sensor == 1:
                    go(11,30)
                    sleep(0.0001)
                    check()
                    
                    if middle_sensor == 0 and right_sensor== 0 and left_sensor == 0:
                        go(11,50)
                        sleep(0.0001)
                        
                else: #no sensors
                    if left_sensor == 0 and right_sensor == 0 and middle_sensor == 0:
                        go(11,11)
                        sleep(0.6)
                        check()
                        if left_sensor == 0 and right_sensor == 0 and middle_sensor == 0:
                            go(0,0)
                            n+=1
                            check()
                            while left_sensor == 0 and right_sensor == 0 and middle_sensor == 0:
                                check()
                            
                                
        except KeyboardInterrupt:
            print("Manual stop on computer")
            quit()
        
except ModuleNotFoundError:
    print('Library not installed and/or not installed correctly!')
