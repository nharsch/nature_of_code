randomCounts = [0.0] * 20


def setup():
    size(640,240)


def draw():
    background(255)

    index = int(random(len(randomCounts)))
    randomCounts[index] +=1

    stroke(0)
    fill(127)

    w = width / len(randomCounts)

    for x, r in enumerate(randomCounts):
        rect(x * w, height - r, w - 1, r)

