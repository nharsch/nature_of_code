def setup():
    size(650, 350)
    background(255)
    global dis, deg
    dis = 100
    deg = 0


def draw():
    global deg
    background(255)

    translate(width/2, height/2);
    rotate(radians(deg))
    deg += 1

    line(-dis, 0, +dis, 0)
    ellipse(-dis, 0, 10, 10)
    ellipse(+dis, 0, 10, 10)

