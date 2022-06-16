# OpenScanner
## TL;DR
Scanner Kit For Bar

## Pipeline

input -> pre process -> engine -> post process



## Q&A
### 不同engine，支持不同数据类型的问题
Q: RT,对于不同的Engine实现，可能需要接收不同的数据，如zbar需要Y800的数据，而华为ScanKit则只能使用Bitmap，如何去平衡图像转换带来的性能开销呢。 <p>
A: 首先，对于任何场景，输入到扫码器中的数据类型是固定的，始终会面临需要转换的问题。因此核心的问题，是数据的转换放在哪个环节比较稳妥。有以下几种考虑
1. 放到Input之前，但是此时是无法获取到engine对于类型的支持的。
2. 放到PreProcess中，预想中前处理模块就是来完成这个工作的，但是对于多种类型的engine，需要引擎模块提供接口，来主动告知preprocess模块需要的数据类型，这在整个pipeline是丑陋的。
   同时，如果一个engine检测出来了结果，另外一个engine可能就不需要检测了，此时就存在了资源的浪费。
   
3. 放到Engine模块中，这里是最合理的环境，对于image的存放，使用map结构 用format作为key，避免相同类型的重复转换。这样,preprocess唯一要做的工作就是将图像根据取景框扣个洞~

### 不同模块目前都运行在同一个线程
Q: 目前所有处理都运行在同一线程上，有无考虑过多线程并行加速处理。
A: 现在整个pipeline都跑在thread模块创建的线程上，但是对于数据的交换，已经通过blockingqueue来完成了，后面的调整是非常简单的，也是必要的。


## TODO

1. benchmark 很重要

2. Appearance 接口 frame 和 listener

3. 缓存统一

4. 区分 InitOption 与 MutableConfig

5. 检测 识别分离

