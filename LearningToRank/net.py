import torch
from torch import nn
import torch.nn.functional as F

class simpleNet(nn.Module):
    def __init__(self, in_dim, n_hidden_1, n_hidden_2, out_dim):
        super(simpleNet, self).__init__()
        self.layer1 = nn.Linear(in_dim, n_hidden_1)
        self.layer2 = nn.Linear(n_hidden_1, n_hidden_2)
        self.layer3 = nn.Linear(n_hidden_2, out_dim)

    def forward(self, x):
        x = self.layer1(x)
        x = self.layer2(x)
        x = self.layer3(x)
        return x

class Activation_Net(nn.Module):
    def __init__(self, in_dim, n_hidden_1, n_hidden_2, out_dim):
        super(Activation_Net, self).__init__()
        self.layer1 = nn.Sequential(nn.Linear(in_dim, n_hidden_1), nn.ReLU(True))
        self.layer2 = nn.Sequential(nn.Linear(n_hidden_1, n_hidden_2), nn.ReLU(True))
        self.layer3 = nn.Sequential(nn.Linear(n_hidden_2, out_dim))


    def forward(self, x):
        x = self.layer1(x)
        x = self.layer2(x)
        self.featuremap = x.detach()
        x = self.layer3(x)
        return x

class Batch_Net(nn.Module):
    def __init__(self, in_dim, n_hidden_1, n_hidden_2, out_dim):
        super(Batch_Net, self).__init__()
        self.layer1 = nn.Sequential(nn.Linear(in_dim, n_hidden_1), nn.BatchNorm1d(n_hidden_1), nn.ReLU(True))
        self.layer2 = nn.Sequential(nn.Linear(n_hidden_1, n_hidden_2), nn.BatchNorm1d(n_hidden_2), nn.ReLU(True))
        self.layer3 = nn.Sequential(nn.Linear(n_hidden_2, out_dim))

    def forward(self, x):
        x = self.layer1(x)
        x = self.layer2(x)
        self.featuremap = x.detach()
        x = self.layer3(x)
        return x

class Network(nn.Module):
    def __init__(self):
        super().__init__()
        self.conv1 = nn.Conv2d(in_channels=1, out_channels=6, kernel_size=(5,1))
        self.conv2 = nn.Conv2d(in_channels=6, out_channels=12, kernel_size=(5,1))

        self.fc1 = nn.Sequential(nn.Linear(12 * 8 * 1, 80,bias=True), nn.BatchNorm1d(80))
        self.fc2 = nn.Sequential(nn.Linear(80, 80,bias=True), nn.BatchNorm1d(80))
        self.out = nn.Sequential(nn.Linear(80, 3,bias=True))
        """self.fc1 = nn.Linear(in_features=12 * 8 * 1, out_features=80)
        self.fc2 = nn.Linear(in_features=80, out_features=80)
        self.out = nn.Linear(in_features=80, out_features=3)"""

    def forward(self, t):
        # (1) input layer
        t = t

        # (2) hidden conv layer
        t = self.conv1(t)
        t = F.relu(t)
        t = F.max_pool2d(t, kernel_size=(2,1), stride=2)

        # (3) hidden conv layter
        t = self.conv2(t)
        t = F.relu(t)
        t = F.max_pool2d(t, kernel_size=(2,1), stride=2)

        # (4) hidden linear layer
        t = t.reshape(-1, 12 * 8 * 1)
        t = self.fc1(t)
        t = F.relu(t)

        # (5) hidden linear layer
        t = self.fc2(t)
        self.featuremap = t.detach()
        t = F.relu(t)

        # (6) output layer
        t = self.out(t)

        return t


class SimpleCNN(torch.nn.Module):
    def __init__(self):
        super(SimpleCNN, self).__init__()  # b, 3, 32, 32
        layer1 = torch.nn.Sequential()
        layer1.add_module('conv1', torch.nn.Conv2d(1, 32, 3, 1, padding=1)) # ?°Î??3?°ß°Í°ß?°Î°›?32?°ß°Í°ß?°Î°›?£§?®C°„32°Í?32

        # b, 32, 32, 32
        layer1.add_module('relu1', torch.nn.ReLU(True))
        layer1.add_module('pool1', torch.nn.MaxPool2d(2, 2))  # b, 32, 16, 16 //°›?a???16*16
        self.layer1 = layer1

        layer2 = torch.nn.Sequential()
        layer2.add_module('conv2', torch.nn.Conv2d(32, 64, 3, 1, padding=1))
        # b, 64, 16, 16 //£§??®¨°›°≠64?°ß°Í°ß 16°Í?16
        layer2.add_module('relu2', torch.nn.ReLU(True))
        layer2.add_module('pool2', torch.nn.MaxPool2d(2, 2))  # b, 64, 8, 8
        self.layer2 = layer2

        layer3 = torch.nn.Sequential()
        layer3.add_module('conv3', torch.nn.Conv2d(64, 128, 3, 1, padding=1))
        # b, 128, 8, 8 //£§??®¨°›°≠128?°ß°Í°ß4*4
        layer3.add_module('rellu3', torch.nn.ReLU(True))
        layer3.add_module('pool3', torch.nn.MaxPool2d(2, 2))  # b 128, 4, 4
        self.layer3 = layer3
        # ?°‰?°ß¶∏°±?°Î°›?
        layer4 = torch.nn.Sequential()
        layer4.add_module('fc1', torch.nn.Linear(1152, 512))
        layer4.add_module('fc_relu1', torch.nn.ReLU(True))
        layer4.add_module('fc2', torch.nn.Linear(512, 64))
        layer4.add_module('fc_relu2', torch.nn.ReLU(True))
        layer4.add_module('fc3', torch.nn.Linear(64, 10))
        self.layer4 = layer4

    def forward(self, x):
        conv1 = self.layer1(x)
        conv2 = self.layer2(conv1)
        conv3 = self.layer3(conv2)
        fc_input = conv3.view(conv3.size(0), -1)
        fc_out = self.layer4(fc_input)
        return fc_out
