import robot
from time import sleep

r = robot.Robot()

def main():
    t =  # Turn rate (degrees per second at full speed)
    v =  # speed (centimeters per second at 90% speed)

    def process_data(position, bearing, distance):
        print("constants:", constants)
        print("position:", position)
        print("bearing:", bearing)
        print("distance:", distance)

    # Shared constants dictionary
    constants = {
        "pos_x": 0,
        "pos_y": 0,
        "bearing_x": 0,
        "bearing_y": 0,
        "distance_x": 0,
        "distance_y": 0,
    }

    class Position:
        def __init__(self, x, y, constants):
            self.x = x
            self.y = y
            self.constants = constants
            self.update_constants()

        def update_constants(self):
            self.constants["pos_x"] = self.x
            self.constants["pos_y"] = self.y

        def move(self, dx, dy):
            self.x += dx
            self.y += dy
            self.update_constants()

        def __str__(self):
            return f"Position(x={self.x}, y={self.y})"

    class Bearing:
        def __init__(self, x, y, constants):
            self.x = x
            self.y = y
            self.constants = constants
            self.update_constants()

        def update_constants(self):
            self.constants["bearing_x"] = self.x
            self.constants["bearing_y"] = self.y

        def change(self, dx, dy):
            self.x += dx
            self.y += dy
            self.update_constants()

        def __str__(self):
            return (f"Bearing(x={self.x}, y={self.y})")

    class Distance:
        def __init__(self, x, y, constants):
            self.x = x
            self.y = y
            self.constants = constants
            self.update_constants()

        def update_constants(self):
            self.constants["distance_x"] = self.x
            self.constants["distance_y"] = self.y

        def update(self, dx, dy):
            self.x += dx
            self.y += dy
            self.update_constants()

        def __str__(self):
            return (f"Distance(x={self.x}, y={self.y})")



    def twist(angle, turn_rate):
        twistT = abs(angle) / turn_rate  # Calculate time needed to twist

        if angle < 0:  # rotate left
            r.motors[0] = -15  # Left motor
            r.motors[1] = 15  # Right motor
        else:  # rotate right
            r.motors[0] = 15  # Left motor
            r.motors[1] = -15  # Right motor

        sleep(twistT)
        r.motors[0] = 0
        r.motors[1] = 0

    def move(distance, speed):
        moveT = distance / speed  # Calculate time needed to move
        r.motors[0] = 90
        r.motors[1] = 90
        sleep(moveT)
        r.motors[0] = 0
        r.motors[1] = 0

    pos = Position(0, 0, constants)
    bear = Bearing(0, 0, constants)
    dist = Distance(0, 0, constants)

    process_data(pos, bear, dist)

    print("position by (5, 3)...")
    pos.move(5, 3)
    process_data(pos, bear, dist)

    print("bearing by (90, -200)...")
    bear.change(90, -200)
    process_data(pos, bear, dist)

    print("distance by (4, 6)...")
    dist.update(4, 6)
    process_data(pos, bear, dist)

    # Robot movement examples
    print("Twisting by 45 degrees...")
    twist(45, turn_rate=30) 

    print("Moving forward by 100 cm...")
    move(100, speed=50)

# Run the main function
if __name__ == "__main__":
    main()
