import random

import numpy as np
# import pytorch

import torch
from torch import nn, optim
from torch.autograd import Variable
from torch.utils.data import DataLoader
from torchvision import datasets, transforms
from scipy import stats

import net

def imbalance(f,train):
    dataMat1 = []
    dataMat2 = []
    dataMat3 = []
    labelMat1 = []
    labelMat2 = []
    labelMat3 = []
    a = 0
    b = 0
    c = 0
    for line in f.readlines():
        curLine = line.strip().split(" ")
        if (curLine[0] == '0'):
            labelMat1.append(curLine[0])
            a += 1
            for each in curLine[2:48]:
                curNode = each.strip().split(":")
                dataMat1.append(curNode[1])
        if (curLine[0] == '1'):
            b += 1
            labelMat2.append(curLine[0])
            for each in curLine[2:48]:
                curNode = each.strip().split(":")
                dataMat2.append(curNode[1])
        if (curLine[0] == '2'):
            c += 1
            labelMat3.append(curLine[0])
            for each in curLine[2:48]:
                curNode = each.strip().split(":")
                dataMat3.append(curNode[1])

    labelMat1 = np.array(labelMat1)
    labelMat2 = np.array(labelMat2)
    labelMat3 = np.array(labelMat3)

    dataMat1 = np.array(dataMat1)
    dataMat2 = np.array(dataMat2)
    dataMat3 = np.array(dataMat3)

    len1 = dataMat1.shape[0] / 46
    dataMat1 = dataMat1.reshape(int(len1), 46)
    dataMat1 = np.hstack((dataMat1, labelMat1.reshape(int(len1), 1)))
    len2 = dataMat2.shape[0] / 46
    dataMat2 = dataMat2.reshape(int(len2), 46)
    dataMat2 = np.hstack((dataMat2, labelMat2.reshape(int(len2), 1)))
    len3 = dataMat3.shape[0] / 46
    dataMat3 = dataMat3.reshape(int(len3), 46)
    dataMat3 = np.hstack((dataMat3, labelMat3.reshape(int(len3), 1)))

    #print(dataMat1.shape, dataMat2.shape, dataMat3.shape)

    dataMat1 = dataMat1.astype(np.float64)
    dataMat2 = dataMat2.astype(np.float64)
    dataMat3 = dataMat3.astype(np.float64)

    if(train==True):
        row_rand_array = np.arange(dataMat1.shape[0])
        np.random.shuffle(row_rand_array)
        dataMat0 = dataMat1[row_rand_array[0:(int(len3 / 100) * 100)]]

        row_rand_array = np.arange(dataMat2.shape[0])
        np.random.shuffle(row_rand_array)
        dataMat2 = dataMat2[row_rand_array[0:(int(len3 / 100) * 100)]]

        dataMat = np.vstack((dataMat0, dataMat2, dataMat3))

        return dataMat
    else:
        dataMat = np.vstack((dataMat1, dataMat2, dataMat3))

        return dataMat

path1 = '/Users/yibowang/Desktop/proj2/MQ2007/Fold1/train.txt'
path2 = '/Users/yibowang/Desktop/proj2/MQ2007/Fold1/vali.txt'
path3 = '/Users/yibowang/Desktop/proj2/MQ2007/Fold1/test.txt'
path4 = '/Users/yibowang/Desktop/proj2/MQ2007/Fold1/best_model.txt'
path5 = '/Users/yibowang/Desktop/proj2/MQ2007/Fold1/output_test.txt'
path6 = '/Users/yibowang/Desktop/proj2/MQ2007/Fold1/output_train.txt'
path7 = '/Users/yibowang/Desktop/proj2/MQ2007/Fold1/output_vali.txt'

out_f = 50
batch_size = 2
learning_rate = 0.05

f1 = open(path1,'r')
train_data = imbalance(f1,True)
train_data = torch.tensor(train_data).float()
if (train_data.shape[0] % batch_size != 0):
    for i in range(batch_size-train_data.shape[0] % batch_size):
        train_data = np.vstack((train_data,train_data[-1]))
train_data = torch.tensor(train_data).float()
#print(train_data.shape)

f1 = open(path1,'r')
train_data2 = imbalance(f1,False)
print(train_data2.shape)
if (train_data2.shape[0] % batch_size != 0):
    for i in range(batch_size-train_data2.shape[0] % batch_size):
        train_data2 = np.vstack((train_data2,train_data2[-1]))
train_data2 = torch.tensor(train_data2).float()

f1 = open(path1,'r')
train_data3 = imbalance(f1,False)

f2 = open(path2,'r')
vali_data = imbalance(f2,False)
if (vali_data.shape[0] % batch_size != 0):
    for i in range(batch_size-vali_data.shape[0] % batch_size):
        vali_data = np.vstack((vali_data,vali_data[-1]))
print(vali_data.shape)
vali_data = torch.tensor(vali_data).float()

f3 = open(path3,'r')
test_data = imbalance(f3,False)
print(test_data.shape)
if (test_data.shape[0] % batch_size != 0):
    for i in range(batch_size-test_data.shape[0] % batch_size):
        test_data = np.vstack((test_data,test_data[-1]))
test_data = torch.tensor(test_data).float()

"""end of processing on imbalance data"""

train_loader = DataLoader(train_data, batch_size=batch_size, shuffle=True)
vali_loader = DataLoader(vali_data, batch_size=batch_size, shuffle=False)
test_loader = DataLoader(test_data, batch_size=batch_size, shuffle=False)
train2_loader = DataLoader(train_data2, batch_size=batch_size, shuffle=True)

#weights = [train_data3.shape[0]/train_data3.shape[0], train_data3.shape[0]/vali_data.shape[0], train_data3.shape[0]/test_data.shape[0]]
#class_weights = torch.FloatTensor(weights)
#model = net.simpleNet(46, 200, 200, 3)
#model = net.Activation_Net(46, 150, 50, 3)
#model = net.Batch_Net(46, 200, 200, 3)
model = net.Network()
criterion = nn.CrossEntropyLoss()
optimizer = optim.SGD(model.parameters(), lr=learning_rate)
#optimizer = optim.Adam(model.parameters(), lr=learning_rate)

eval_loss = 0
eval_acc = 0

def get_num_correct(preds, labels):
    #print(preds.argmax(dim=1),labels)
    return preds.argmax(dim=1).eq(labels).sum().item()

MinLoss = 100
for i in range(20):
    epoch = 0
    for data in train_loader:
        x = data[:,0:46]
        x = x.view(batch_size, 1, 46, 1)
        #out = model(data[:,0:46])
        out = model(x)
        #loss = criterion(out, data[:,46].type(torch.LongTensor))
        loss = criterion(out, data[:, 46].type(torch.LongTensor))
        optimizer.zero_grad()
        loss.backward()
        optimizer.step()
        epoch += 1
        #if epoch % 50 == 0:
            #print('epoch: {}, loss: {:.4}'.format(epoch, loss.data.item()))

    model.eval()
    eval_loss = 0
    eval_acc = 0
    epoch = 0
    for data in vali_loader:
        x = data[:, 0:46]
        x = x.view(batch_size, 1, 46, 1)
        #out = model(data[:,0:46])
        out = model(x)
        loss = criterion(out, data[:,46].type(torch.LongTensor))
        eval_loss += loss.data.item() * batch_size
        _, pred = torch.max(out, 1)
        eval_acc += get_num_correct(out, data[:,46].type(torch.LongTensor))
        epoch += 1
    print('Test Loss: {:.6f}, Acc: {:.6f}'.format(
        eval_loss / (len(vali_data)),
        eval_acc / (len(vali_data))))
    if((eval_loss / (len(vali_data))) < MinLoss):
        MinLoss = eval_loss / (len(vali_data))
        torch.save(model.state_dict(),path4)

model = net.Network()
model.load_state_dict(torch.load(path4))
eval_loss = 0
eval_acc = 0
features = np.empty([1,out_f])
for data in test_loader:
    x = data[:, 0:46]
    x = x.view(batch_size, 1, 46, 1)
    out = model(x)
    #out = model(data[:, 0:46])
    print(out.argmax(dim=1),data[:, 46].type(torch.LongTensor))
    feature_output = model.featuremap.transpose(1,0)
    #feature_output = feature_output.reshape(1, out_f)
    feature_output = torch.transpose(feature_output, 0, 1)
    features = np.vstack((features, feature_output))
    loss = criterion(out, data[:, 46].type(torch.LongTensor))
    eval_loss += loss.data.item() * batch_size
    _, pred = torch.max(out, 1)
    eval_acc += get_num_correct(out, data[:, 46].type(torch.LongTensor))
    #epoch += 1
print('-----------------------------')
#print(len(train_data),len(vali_data),len(test_data))
print('Test Loss: {:.6f}, Acc: {:.6f}'.format(
    eval_loss / (len(test_data)),
    eval_acc / (len(test_data))))

print(features.shape)
#features = stats.zscore(features)

ff = open(path5, 'w')
num = 1
f3 = open(path3, 'r')
for line in f3.readlines():
    curLine = line.strip().split(" ")
    for i in range(48):
        ff.write(str(curLine[i]) + " ")
    for j in range(0,out_f):
        ff.write(str(j+47) + ":" + str(features[num][j]) + " ")
    for k in range(48,57):
        ff.write(str(curLine[k]) + " ")
    ff.write('\n')
    num+=1
    if(num==features.shape[0]+1):
        break

#train
model.load_state_dict(torch.load(path4))
eval_loss = 0
eval_acc = 0
features = np.empty([1,out_f])
for data in train2_loader:
    x = data[:, 0:46]
    x = x.view(batch_size, 1, 46, 1)
    #out = model(data[:, 0:46])
    out = model(x)
    feature_output = model.featuremap.transpose(1,0)
    #feature_output = feature_output.reshape(1, out_f)
    feature_output = torch.transpose(feature_output, 0, 1)
    features = np.vstack((features, feature_output))
    loss = criterion(out, data[:, 46].type(torch.LongTensor))
    eval_loss += loss.data.item() * batch_size
    _, pred = torch.max(out, 1)
    eval_acc += get_num_correct(out, data[:, 46].type(torch.LongTensor))
    #epoch += 1
print('-----------------------------')
print(len(train_data),len(vali_data),len(test_data))
print('Train Loss: {:.6f}, Acc: {:.6f}'.format(
    eval_loss / (len(train_data2)),
    eval_acc / (len(train_data2))))

print(features.shape)
#features = stats.zscore(features)

ff = open(path6, 'w')
num = 1
f1 = open(path1, 'r')
for line in f1.readlines():
    curLine = line.strip().split(" ")
    for i in range(48):
        ff.write(str(curLine[i]) + " ")
    for j in range(0,out_f):
        ff.write(str(j+47) + ":" + str(features[num][j]) + " ")
    for k in range(48,57):
        ff.write(str(curLine[k]) + " ")
    ff.write('\n')
    num+=1
    if(num==features.shape[0]+1):
        break

# validate
model.load_state_dict(torch.load(path4))
eval_loss = 0
eval_acc = 0
features = np.empty([1,out_f])
for data in vali_loader:
    x = data[:, 0:46]
    x = x.view(batch_size, 1, 46, 1)
    #out = model(data[:, 0:46])
    out = model(x)
    feature_output = model.featuremap.transpose(1,0)
    #feature_output = feature_output.reshape(1, out_f)
    feature_output = torch.transpose(feature_output, 0, 1)
    features = np.vstack((features, feature_output))
    # loss = criterion(out, data[:, 46].type(torch.LongTensor))
    eval_loss += loss.data.item() * batch_size
    _, pred = torch.max(out, 1)
    eval_acc += get_num_correct(out, data[:, 46].type(torch.LongTensor))
    #epoch += 1
print('-----------------------------')
print(len(train_data),len(vali_data),len(test_data))
print('Vali Loss: {:.6f}, Acc: {:.6f}'.format(
    eval_loss / (len(vali_data)),
    eval_acc / (len(vali_data))))

print(features.shape)
#features = stats.zscore(features)

ff = open(path7, 'w')
num = 1
f2 = open(path2, 'r')
for line in f2.readlines():
    #print(1)
    curLine = line.strip().split(" ")
    for i in range(48):
        ff.write(str(curLine[i]) + " ")
    for j in range(0,out_f):
        ff.write(str(j+47) + ":" + str(features[num][j]) + " ")
    for k in range(48,57):
        ff.write(str(curLine[k]) + " ")
    ff.write('\n')
    num+=1
    if(num==features.shape[0]+1):
        break


#java -jar RankLib-2.9.jar -train "E:\phd tools\ML_Problem2\output_train.txt" -test "E:\phd tools\ML_Problem2\output_test.txt" -validate "E:\phd tools\ML_Problem2\output_vali.txt" -ranker 6 -metric2t NDCG@10 -metric2T NDCG@10 -save mymodel.txt

#java -jar RankLib-2.9.jar -train "E:\phd tools\My_IR\MQ2007\Fold1\output_train.txt" -test "E:\phd tools\My_IR\MQ2007\Fold1\output_test.txt" -validate "E:\phd tools\My_IR\MQ2007\Fold1\output_vali.txt"  -ranker 6 -metric2t NDCG@2 -metric2T NDCG@2 -save 2007-Fold1-model-ndcg2.txt

