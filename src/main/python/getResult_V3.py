import matplotlib.pyplot as plt
import array
import os
from numpy.core.fromnumeric import mean, size
plt.rcParams['font.sans-serif'] = ['Times New Roman']
plt.rcParams['axes.unicode_minus'] = False
from scipy import fftpack 
from scipy.signal import hilbert
import numpy as np
import pandas as pd
import math
from scipy.linalg import toeplitz
from scipy.io import loadmat
from copy import deepcopy
from scipy.signal import filtfilt as filter
from os.path import join,abspath
from scipy.signal import find_peaks

class Result():
    fs = None#采样频率
    # 设置参数
    L = 50#滤波器长度
    M = 3#移位数
    erc = 1e-10#收敛标准
    R = 50#最大迭代次数   
    x = []
    x.clear()
    t = []
    sp = None
    fHs = None
    fre = None
    useData = 10000
    def __init__(self,readPath,savePath,fs):
        self.readPath = readPath
        self.savePath = savePath
        self.fs = fs
        if(self.readPath.endswith('.mat')):
            # m = h5py.File(self.readPath,'r')
            m = loadmat(self.readPath)
            x1=m['x']
            dataSize = x1.size
            startIndex = int((dataSize-9800)/2)
            self.x.clear()
            if(len(x1[0])!=1):
                self.x.append(x1[0][startIndex:startIndex+self.useData])
            else:
                x1 = x1.T
                self.x.append(x1[0][startIndex:startIndex+self.useData])
            self.x=np.array(self.x)
        elif(self.readPath.endswith('.pcm')):
            file = open(self.readPath, 'rb')
            shortArray = array.array('h') # int16
            size = int(os.path.getsize(self.readPath) / shortArray.itemsize)
            shortArray.fromfile(file, size) # faster than struct.unpack
            file.close()
            dataSize = len(shortArray)
            startIndex = int((dataSize-10000)/2)
            self.x.clear()
            self.x.append(shortArray[startIndex:startIndex+self.useData])
            self.x=np.array(self.x)/(1<<15)
        self.SP = self.useData  #信号点数
        self.t = [i/self.useData for i in range(self.useData)]
    def getData(self):
        x_mean = self.x.mean()
        x_std = self.x.std()
        x_rms = math.sqrt(pow(x_mean,2)+pow(x_std,2))
        x_skew = pd.Series(self.x[0]).skew()
        x_kurt = pd.Series(self.x[0]).kurt()
        x_ff = max(self.x[0]) - min(self.x[0])
        return x_ff,x_skew,x_kurt,x_rms
    def IMCKD_f(self,x,L,erc,M,R):
        T_se=[]
        ckm=[]
        error=[]
        f=np.zeros(L);f[1]=1
        F=np.array([f])
        S=np.array(x)
        y=np.append(x[0][0],np.zeros(L-1))
        X0=toeplitz(y,x)#原信号决定的矩阵
        T=self.T_search(x)
        T_se.append(T)#估计周期
        ckm.append(self.CKM(x,M,T_se[0]))#计算相关峭度
        # 迭代
        i=0
        er=1
        while er>erc or i<R:
            X_Shift = self.shift(x,M,T)
            S=np.append(S,np.dot(F[[i],:],X0),0)  #滤波
            Y_Shift = self.shift(S[i+1],M,T)
            b1 = np.prod(Y_Shift,axis=0)
            A = np.sum(S[i+1]**2)/np.sum(b1**2)/(M+1)
            XM = np.zeros((M+1,L))
            for m in range(M+1):
                y = X_Shift[m]
                y1=np.append(y[0],np.zeros((L-1)))
                X = toeplitz(y1,y)#生成XmT
                Shift2 = deepcopy(Y_Shift)
                Shift2[[m],:] = np.ones(x.size)
                b2 = np.prod(Shift2,axis=0)
                a = b1*b2  #生成αm（行向量）
                XM[[m],:] = np.dot(a,X.T)  #XM的每一行都是XmT乘上αm
            f = (A*np.dot(np.linalg.pinv(np.dot(X0,X0.T)),np.array([np.sum(XM,axis=0)]).T)).T
            f = f/np.sqrt(np.sum(np.multiply(f,f)))  #放缩以保持滤波后的信号幅值
            F=np.append(F,f,axis=0)
            T = self.T_search(S[[i+1],:])  #估计周期
            T_se.append(T)
            ckm.append(self.CKM(S[[i+1],:],M,T_se[i+1]))  #计算相关峭度
            er = abs(ckm[i+1]-ckm[i])
            error.append(er)    
            i = i+1
        f_final = f
        s = filter(f[0],1,x[0])
        count = i
        return f_final,s,ckm,error,T_se,count
    
    def shift(self,x,M,T):
        if x.ndim>1:
            x=x[0]
        N=x.size
        T+=1
        Shift=np.zeros((M+1,N))
        for m in range(M+1):
            x_shift=np.zeros(N)
            for j in range(m*T,N):
                # x=np.array(x)
                x_shift[j]=x[j-m*T]
            Shift[[m],:]=x_shift
        return Shift
    
    def CKM(self,x,M,T):
        Shift=self.shift(x,M,T)
        Numerator=np.sum(np.prod(Shift,axis=0)**2)
        Denominator=(np.sum(x**2))**(M+1)
        Correlated_Kurtosis_M=Numerator/Denominator
        return Correlated_Kurtosis_M

    def T_search(self,x):
        envelope_x=np.abs(hilbert(x))
        envelope_x=envelope_x-np.mean(envelope_x)
        autocorrelation_envelope_x=np.correlate(envelope_x.flatten(),envelope_x.flatten(),mode='full')
        autocorrelation_envelope_x=autocorrelation_envelope_x[x.size-1:]
        index1=0
        while autocorrelation_envelope_x[index1]>0 and autocorrelation_envelope_x[index1+1]>0:
            index1=index1+1
        
        part_autocorre=autocorrelation_envelope_x[index1+1:]
        p=np.argmax(part_autocorre)+1
        T=index1+p
        return T

    def Tr(self,x):
        X=abs(np.fft.fft(x))/len(x)*2
        return X[:math.floor(len(X)/2)]

    def pic(self):
        f_final,s,ckm,error,T_se,count = self.IMCKD_f(self.x,self.L,self.erc,self.M,self.R)

        fig, ax = plt.subplots(figsize=(10,6))
        ax.ticklabel_format(axis='y', style='scientific', scilimits=(1,10))
        ax.spines['left'].set_position(('data', 0))
        plt.title('IMCKD Filtered Signal',fontsize=15)
        plt.xticks(fontsize=10)
        plt.yticks(fontsize=10)
        plt.ylabel('Amplitude',fontsize=12)
        plt.xlabel('Time(s)',fontsize=12)
        plt.plot(self.t, s,linewidth=2,c="#0000ff")
        plt.tight_layout()
        plt.savefig(join(abspath(self.savePath),"dataPic2.png"))
        plt.clf()
        # 包络分析&包络谱展示
        #对原信号进行包络分析
        Hx=abs(hilbert(self.x))
        Hx=Hx-np.mean(Hx)
        Hx=Hx[0]
        fHx=self.Tr(Hx)
        #对MED滤波后的信号进行包络分析
        Hs=abs(hilbert(s))
        Hs=Hs-np.mean(Hs)
        self.fHs=self.Tr(Hs)

        self.fre=np.arange(fHx.size)/((self.SP-1)/self.fs)#频谱的横坐标

        fig, ax = plt.subplots(figsize=(10,6))
        ax.ticklabel_format(axis='y', style='scientific', scilimits=(1,10))
        ax.spines['left'].set_position(('data', 0))
        ax.spines['bottom'].set_position(('data', 0))
        plt.title('Original Signal Envelope Spectrum',fontsize=15)
        plt.xticks(fontsize=10)
        plt.yticks(fontsize=10)
        plt.ylabel('Amplitude',fontsize=12)
        plt.xlabel('Frequency(Hz)',fontsize=12)
        plt.plot(self.fre[:150],fHx[:150],linewidth=2,c="#0000ff")
        plt.tight_layout()
        plt.savefig(join(abspath(self.savePath),"dataPic3.png"))
        plt.clf()

        fig, ax = plt.subplots(figsize=(10,6))
        ax.ticklabel_format(axis='y', style='scientific', scilimits=(1,10))
        ax.spines['left'].set_position(('data', 0))
        ax.spines['bottom'].set_position(('data', 0))
        plt.title('IMCKD Filtered Signal Envelope Spectrum',fontsize=15)
        plt.xticks(fontsize=10)
        plt.yticks(fontsize=10)
        plt.ylabel('Amplitude',fontsize=12)
        plt.xlabel('Frequency(Hz)',fontsize=12)
        plt.plot(self.fre[:150],self.fHs[:150],linewidth=2,c="#0000ff")
        plt.tight_layout()
        plt.savefig(join(abspath(self.savePath),"dataPic4.png"))
        plt.clf()

        # 故障信号周期估计值的展示
        # fig3_1 = plt.figure(5)
        # # plt.subplot(311)
        # plt.plot(ckm)
        # plt.title('Kurtosis')
        # plt.savefig(join(abspath(self.savePath),"dataPic5.png"))
        # plt.clf()
        # # plt.subplot(312)
        # fig3_2 = plt.figure(6)
        # plt.plot(error)
        # plt.title('Correlation Kurtosis Change Value')
        # plt.savefig(join(abspath(self.savePath),"dataPic6.png"))
        # plt.clf()
        # # plt.subplot(313)
        # fig3_3 = plt.figure(7)
        # plt.plot(T_se)
        # plt.title('Estimation of Fault Signal Period')
        # # 定义函数——作DFT并取前一半谱线
        # plt.savefig(join(abspath(self.savePath),"dataPic7.png"))
        # plt.clf()

    def lisjudgetimes(self,x):
        if len(x)<3:
            return 0
        count=0
        for i in range(len(x)):
            count+=abs(x[i]-x[0]*(i+1))
            error=len(x)
        if count<error:
           return 1
        else:
           return 0
    def getfeaturepinlv(self):   #有检测频率上限
        indices = find_peaks(self.fHs[:220],height=2*mean(self.fHs[:220]),distance=1)
        a = max(self.fHs[:220])
        for i in list(indices[0]):
            if(i>indices[0][-1]/2):
                return -1
            if((2*i in list(indices[0]))and(3*i in list(indices[0]))):
                if(i%2!=0):
                    print(list(indices[0]))
                    if(int(i/2) in list(indices[0]) and self.fHs[int(i/2)]==a):
                        return self.fre[int(i/2)]
                    if(int(i/2+1) in list(indices[0]) and self.fHs[int(i/2+1)]==a):
                        return self.fre[int(i/2+1)]
                    else:
                        return self.fre[i]
                else:
                    return self.fre[i]
        
def run(readPath,savePath,fs):
    data = Result(readPath,savePath,fs)
    x_ff,x_skew,x_kurt,x_rms = data.getData()
    data.pic()
    f = open(savePath+'/data10.txt','w')
    f.write(str(format(data.getfeaturepinlv(),".2f"))+"\n")
    f.write(str(format(x_ff,".2f"))+"\n")
    f.write(str(format(x_skew,".2f"))+"\n")
    f.write(str(format(x_kurt,".2f"))+"\n")
    f.write(str(format(x_rms,".2f"))+"\n")
    f.close