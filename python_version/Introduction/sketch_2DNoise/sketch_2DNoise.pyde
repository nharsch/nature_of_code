t = 0
noiseDetail(12)

def setup():
    loadPixels()
    size(200,200)
    frameRate(1)


def draw():
    global t
    t +=1
    xoff = t

    for x in range(0, width):
        global t
        yoff = t
        for y in range(0, height):
            bright = map(noise(xoff,yoff),0,1,0,255)
            pixels[x+y*+width] = color(bright)
            yoff +=0.03
        xoff += 0.01

    updatePixels()
