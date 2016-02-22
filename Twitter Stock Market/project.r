require(e1071)
data<-read.csv("C:\\Users\\Marie\\Documents\\Project\\Data\\results.csv", header=F)

data[,11] = as.factor(data[,11])
data[,12] = as.factor(data[,12])
data[,13] = as.factor(data[,13])

# plotting sentiment against D&J
plot(c(1:20), data[1:20,8]/data[1,8], type = "l", ylim=c(0.9,1.2), col = 3, ylab = "units", xlab = "day")
points(c(1:20), (data[1:20,2]- 0.900)/(data[1,2]- 0.900), col = 4, type = "l")

legend(3, 1.15, c("happy tweets", "dow jones index"), col = c(4,3), pch=1)

# Splitting into train and test data.
traindata = data[1:55,]
testdata = data[-(1:55),]

C <- 2^(-10)

C = 2*C

# Creating SVM model
obj <- tune(svm, V13 ~ V2 + V3 + V4 + V5 + V6 + V7, data = traindata,
            ranges = list(gamma = 2^(-1:1), cost = 2^(2:4)),
            tunecontrol = tune.control(sampling = "cross")
)

obj$best.parameters

model <- svm(V13 ~ V2 + V3 + V4 + V5 + V6 + V7, data = traindata,  gamma = 0.5, cost = 16)

# Testing the model on the train data.
Y<-predict(model,testdata[,c(2,3,4,5,6,7)])

# Total Error.
Err=(sum(as.numeric(Y) != as.numeric(testdata[,13])))/length(testdata[,13])

Y


# Trying to find out how much we could win using the predicted strategy if we use 10% of our capital
# to buy stock on a day when model predicts that tomorrow it will go up.
stocks = 0
money =  testdata[1, 10] * 1000 * 2
money_original = money

for(i in 1:17) {
  capital = money + stocks * testdata[i,10]
  number_of_stocks_to_sell = min(as.integer(capital * 0.10 / testdata[i,10], stocks))
  number_of_stocks_to_buy = min(as.integer(capital * 0.10 / testdata[i,10]), as.integer(money / testdata[i,10]))
                                 
  if (Y[i] == -1){
    money = money +  number_of_stocks_to_sell * testdata[i,10]
    stocks = stocks - number_of_stocks_to_sell
  }
  if (Y[i] == 1) {
    stocks = stocks + number_of_stocks_to_buy
    money = money - number_of_stocks_to_buy * testdata[i,10] 
  }
}

# printing the ratio new_capital / original_capital
print((stocks * testdata[18,10] + money)/money_original)
print(testdata[18,10] / testdata[1,10])


