import math

selfpos = [0,-3,90] #[x, y, angle from x axis]

def rotate_tags(tags, zone):
    rotation = (zone + 2) % 4
    for tag in tags:
        for i in range(rotation):
            x = tag[0]
            y = tag[1]
            tag[0] = -1*y
            tag[1] = x
        tag[2] += 90*rotation
        tag[2] %= 360

wall_tags = [
    [-2.5, 3, 90],
    [-1.5, 3, 90],
    [-0.5, 3, 90],
    [0.5, 3, 90],
    [1.5, 3, 90],
    [2.5, 3, 90],
    [3, 2.5, 0],
    [3, 1.5, 0],
    [3, 0.5, 0],
    [3, -0.5, 0],
    [3, -1.5, 0],
    [3, -2.5, 0],
    [2.5, -3, 270],
    [1.5, -3, 270],
    [0.5, -3, 270],
    [-0.5, -3, 270],
    [-1.5, -3, 270],
    [-2.5, -3, 270],
    [-3, -2.5, 180],
    [-3, -1.5, 180],
    [-3, -0.5, 180],
    [-3, 0.5, 180],
    [-3, 1.5, 180],
    [-3, 2.5, 180],
]

def compute_vector(dist, angle):
    vector = [round(dist * math.cos(math.radians(angle)), 4), round(dist * math.cos(math.radians(90-angle)), 4)]
    return vector

def pos_update(distance, angle):
    global selfpos
    coords = compute_vector(distance, selfpos[2])
    selfpos[0] += coords[0]
    selfpos[1] += coords[1]
    selfpos[2] += angle
    selfpos[2] %= 360
    selfpos[2] = round(selfpos[2], 4)
        
#rotate_tags(wall_tags,0)
#print(wall_tags)
#print(compute_vector(5, 210))
#arena_update(0.05, 30, 50, 100)
#print(selfpos)
