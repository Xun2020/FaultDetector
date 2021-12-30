import matplotlib.pyplot as plt
from scipy.io import loadmat
import numpy as np
import os
import array
plt.rcParams['font.sans-serif'] = ['Times New Roman']
plt.rcParams['axes.unicode_minus'] = False
from os.path import dirname,join,abspath


def pic1(readPath,savePath):
    x = []
    useData = 10000
    if(readPath.endswith('.mat')):
        m = loadmat(readPath)
        x1=m['x']
        dataSize = x1.size
        startIndex = int((dataSize-10000)/2)
        x.clear()
        if(len(x1[0])!=1):
            x.append(x1[0][startIndex:startIndex+useData])
        else:
            x1 = x1.T
            x.append(x1[0][startIndex:startIndex+useData])
        x=np.array(x)
    elif(readPath.endswith('.pcm')):
        file = open(readPath, 'rb')
        shortArray = array.array('h') # int16
        size = int(os.path.getsize(readPath) / shortArray.itemsize)
        shortArray.fromfile(file, size) # faster than struct.unpack
        file.close()
        dataSize = len(shortArray)
        startIndex = int((dataSize-10000)/2)
        x.clear()
        x.append(shortArray[startIndex:startIndex+useData])
        x=np.array(x)/(1<<15)
    t = [i/useData for i in range(useData)]
    fig, ax = plt.subplots(figsize=(10,6))
    ax.ticklabel_format(axis='y', style='scientific', scilimits=(1,10))
    ax.spines['left'].set_position(('data', 0))
    plt.title('Original Signal',fontsize=15)
    plt.xticks(fontsize=10)
    plt.yticks(fontsize=10)
    plt.ylabel('Amplitude',fontsize=12)
    plt.xlabel('Time(s)',fontsize=12)
    plt.plot(t, x[0],linewidth=2,c="#0000ff")
    plt.tight_layout()
    plt.savefig(join(abspath(savePath),"dataPic1.png"))
    plt.clf()