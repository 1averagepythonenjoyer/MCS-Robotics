import smbus2

bus = smbus2.SMBus(1)
r = robot.Robot() #dont need in main

def set_servos_to_angle(angle):
 #angle range = 1700 , 4600, 7500
 #mid value 70
 r.servos[0].mode = robot.PWM_SERVO 
 value = 1700 + int(angle/180*5800)
 bus.write_byte_data(0x08, 1, value >> 8)
 bus.write_byte_data(0x08, 2, value & 0xff)
