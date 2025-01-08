import math

def determine_pos_multiple(points, distances, bearings):
    results = []
    for i in range(len(points)):
        pointA, pointB = points[i]
        dis_A, dis_B = distances[i]
        bearing_A, bearing_B = bearings[i]

        # Convert bearings to radians
        bearing_A = math.radians(bearing_A)
        bearing_B = math.radians(bearing_B)

        x1, y1 = pointA
        x2, y2 = pointB

        # Calculate new positions based on distances and bearings
        x_newA = x1 + dis_A * math.cos(bearing_A)
        y_newA = y1 + dis_A * math.sin(bearing_A)
        x_newB = x2 + dis_B * math.cos(bearing_B)
        y_newB = y2 + dis_B * math.sin(bearing_B)

        # Average the positions to determine the final point
        x = (x_newA + x_newB) / 2
        y = (y_newA + y_newB) / 2

        results.append((x, y))

    return results


points = [((100, 101), (200, 201)), ((150, 151), (250, 251))]
distances = [(10, 10), (15, 15)]
bearings = [(74, 89), (60, 75)]

results = determine_pos_multiple(points, distances, bearings)
for i, result in enumerate(results):
    print(f"{i + 1}: {result}")








