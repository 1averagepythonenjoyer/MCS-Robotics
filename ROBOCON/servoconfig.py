import smbus2

 
# Constants from greengiant.py

_GG_I2C_ADDR = 0x08  # I2C address of the Green Giant/PiLow board

 

# Function to write high and low bytes to I2C

def writehighlowdata(bus, address, data):

    value = int(data)

    bus.write_byte_data(_GG_I2C_ADDR, address, value >> 8)  # Write high byte

    bus.write_byte_data(_GG_I2C_ADDR, address + 1, value & 0xff)  # Write low byte

 

# Function to set PWM value for a specific servo

def set_servo_pwm(bus, servo_index, angle):

   # Map the input angle (0-180) to the PWM value range (1500-7500)

    pwm_value = int(1500 + (angle / 180) * 6000)

 

    # Determine the PWM base address for the servo

    pwm_base_address = 1 + (servo_index * 2)  # PWM base addresses start at 1 and increment by 2 for each servo

 

    # Write the PWM value to the I2C device

    writehighlowdata(bus, pwm_base_address, pwm_value)

    print(f"Set servo {servo_index} to {angle}° (PWM value: {pwm_value})")

 

# Main function

def main():

    # Initialize I2C bus

    bus = smbus2.SMBus(1)  # Use I2C bus 1

 

    try:

        # Example: Set servo 0 to 90° (middle position)

        servo_index = 0  # Servo index (0-3)

        angle = 90  # Angle in degrees (0-180)

        set_servo_pwm(bus, servo_index, angle)

 

    finally:

        # Close the I2C bus

        bus.close()

 

if __name__ == "__main__":

    main()
