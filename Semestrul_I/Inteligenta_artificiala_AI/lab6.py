
judete = ['IS', 'VS', 'GL', 'TL', 'CT', 'CL', 'GR', 'TR', 'OT', 'DJ', 'MH', 'CS', 'TM', 'AR',
          'BH', 'SM', 'MM', 'SV', 'BT', 'NT', 'BC', 'VN', 'BR', 'IL', 'IF', 'DB', 'AG', 'VL',
          'GJ', 'HD', 'AB', 'CJ', 'SJ', 'BN', 'HR', 'CV', 'BV', 'SB', 'MS', 'BZ', 'PH']
vecini = [
    ['BT', 'SV', 'NT', 'VS'],
    ['IS', 'BC', 'GL'],
    ['VS', 'VN', 'BR', 'TL'],
    ['BR', 'GL', 'CT'],
    ['TL', 'BR', 'IL', 'CL'],
    ['IL', 'CT', 'IF', 'GR'],
    ['IF', 'TR', 'CL', 'DB'],
    ['OT', 'AG', 'DB', 'GR'],
    ['DJ', 'VL', 'AG', 'TR'],
    ['MH', 'GJ', 'VL', 'OT'],
    ['CS', 'GJ', 'DJ'],
    ['TM', 'HD', 'GJ', 'MH'],
    ['AR', 'HD', 'CS'],
    ['BH', 'AB', 'HD', 'TM'],
    ['AR', 'AB', 'CJ', 'SJ', 'SM'],
    ['BH', 'SJ', 'MM'],
    ['SM', 'SJ', 'CJ', 'BN'],
    ['MM', 'BN', 'MS', 'HR', 'NT', 'IS', 'BT'],
    ['SV', 'NT', 'IS'],
    ['SV', 'BT', 'IS', 'VS', 'BC', 'HR'],
    ['NT', 'VS', 'VN', 'CV', 'HR'],
    ['BC', 'VS', 'GL', 'BR', 'BZ', 'CV'],
    ['VN', 'GL', 'TL', 'CT', 'IL', 'BZ'],
    ['BZ', 'BR', 'CT', 'CL', 'IF', 'PH'],
    ['DB', 'PH', 'IL', 'CL', 'GR'],
    ['PH', 'IF', 'GR', 'TR', 'AG', 'BV'],
    ['DB', 'TR', 'OT', 'VL', 'SB', 'BV'],
    ['AG', 'OT', 'DJ', 'GJ', 'HD', 'SB'],
    ['MH', 'CS', 'HD', 'VL', 'DJ'],
    ['CS', 'GJ', 'AB', 'VL', 'AR', 'TM'],
    ['HD', 'SB', 'MS', 'CJ', 'BH', 'AR'],
    ['BH', 'SJ', 'MM', 'BN', 'MS', 'AB'],
    ['BH', 'SM', 'MM', 'CJ'],
    ['MM', 'CJ', 'MS', 'SV'],
    ['SV', 'NT', 'BC', 'CV', 'BV', 'MS'],
    ['HR', 'BC', 'VN', 'BZ', 'BV'],
    ['HR', 'CV', 'PH', 'DB', 'AG', 'SB', 'MS'],
    ['MS', 'BV', 'AG', 'VL', 'AB'],
    ['BN', 'HR', 'BV', 'SB', 'AB', 'CJ'],
    ['CV', 'VN', 'BR', 'IL', 'PH', 'BV'],
    ['BV', 'BZ', 'IF', 'DB']
]
culori = [['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'],
          ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'],
          ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'],
          ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'],
          ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'],
          ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'],
          ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'],
          ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'], ['R','G','B','V'],
          ['R','G','B','V']]
assignment = ['', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '']

# judete = ['SV', 'BT', 'IS', 'NT', 'BC', 'VN', 'VS', 'GL']
# vecini = [
#     ['BT', 'IS', 'NT'],
#     ['SV','IS'],
#     ['SV', 'BT', 'NT', 'VS'],
#     ['SV', 'IS', 'BC'],
#     ['NT', 'VS', 'VN'],
#     ['BC', 'GL'],
#     ['IS', 'BC', 'GL'],
#     ['VS', 'VN']]
# culori = [['R','G','B'], ['R','G','B'], ['R','G','B'], ['R','G','B'], ['R','G','B'],
#     ['R','G','B'], ['R','G','B'], ['R','G','B']]
# assignment = ['', '', '', '', '', '', '', '']

def assignmentIsFull():
    for i in assignment:
        if i == '':
            return False
    return True

def selectJudet():
    index_judet = 0
    for i in range(0, len(vecini)):
        if len(vecini[i]) > index_judet and len(culori[i]) > 1:
            index_judet = i
    return index_judet

def getVecini(judet):
    veciniList = []
    for index in range(0, len(judete)):
        if judete[index] == judet:
            veciniList.append(vecini[index])
    return veciniList[0]

def getJudetIndex(judet):
    for index in range(0, len(judete)):
        if judete[index] == judet:
            return index

def forward_checking(index_judet, color):
    list_ = []
    for i in culori[index_judet]:
        if color != i:
            list_.append(i)
    if len(list_) == 0:
        return False
    for i in list_:
        culori[index_judet].remove(i) 

    veciniList = getVecini(judete[index_judet])
    for vecin in veciniList:
        for col in culori[getJudetIndex(vecin)]:
            if col == color:
                culori[getJudetIndex(vecin)].remove(col)
    return True


def BKT(judete):
    if assignmentIsFull():
        print("Done! Assignment is full.")
        return
    index_judet = selectJudet()
    for color in culori[index_judet]:
        assignment[index_judet] = color
        forward_checking(index_judet, color)
        print(assignment)
        BKT(judete)

BKT(judete)
