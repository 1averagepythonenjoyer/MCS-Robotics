import math

selfpos = [None]*3

def rotate_tags(tags, zone):
    rotation = (zone + 2) % 4
    for tag in tags:
        for i in range(rotation):
            x = tag[0]
            y = tag[1]
            tag[0] = -1*y
            tag[1] = x
        tag[2] -= 90*rotation
        tag[2] %= 360
        if tag[2] > 180:
            tag[2] -= 360

wall_tags = [
    [-2.5, 3, 0],
    [-1.5, 3, 0],
    [-.0.5, 3, 0],
    [.0.5, 3, 0],
    [1.5, 3, 0],
    [2.5, 3, 0],
    [3, 2.5, 90],
    [3, 1.5, 90],
    [3, 0.5, 90],
    [3, -0.5, 90],
    [3, -1.5, 90],
    [3, -2.5, 90],
    [2.5, -3, 180],
    [1.5, -3, 180],
    [0.5, -3, 180],
    [-0.5, -3, 180],
    [-1.5, -3, 180],
    [-2.5, -3, 180],
    [-3, -2.5, -90],
    [-3, -1.5, -90],
    [-3, -0.5, -90],
    [-3, 0.5, -90],
    [-3, 1.5, -90],
    [-3, 2.5, -90],
]

def compute_vector(dist, angle):
    if angle < 0:
        angle_to_x_axis = -90+angle
    else:
        angle_to_x_axis = 90-angle
    vector = [round(dist * math.cos(math.radians(angle_to_x_axis)), 5), round(dist * math.cos(math.radians(angle)), 5)]
    return vector

def pos_update(distance, bearing, rotation, id):
    id -= 100
    tag_normal = wall_tags[id][2]
    global selfpos
    selfpos[2] = tag_normal + rotation - bearing
    vector_to_tag = compute_vector(distance, tag_normal + bearing)
    #print(vector_to_tag)
    selfpos[0] = wall_tags[id][0] - vector_to_tag[0]
    selfpos[1] = wall_tags[id][1] - vector_to_tag[1]

#pos_update(2, 30, 60, 120)
#print(selfpos)
#rotate_tags(wall_tags, 3)
#print(wall_tags)
