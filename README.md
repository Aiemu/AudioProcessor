# 声音信号处理实验报告
2017011438 曾正

## 0. 实验目的
- 掌握使用 MATLAB /安卓/ python / C++ 等编程语言生成、发送以及采样特定的声音信号
- 理解信号的频率、振幅、相位等基本特征
</br></br>

## 1. 实现一个可以生成符合条件的声波信号的函数
略，同`3.b`，将在`3`中详细介绍
</br></br>

## 2. 实现一个可读取音频文件的函数并绘制信号波形图
- ### 实验结果
裁剪结果如下：
<div style="display: flex;">
  <div style="width: 100%;">
    <img src="https://tva1.sinaimg.cn/large/007S8ZIlly1gjnh6e81p9j30yp0u019l.jpg">
    <div style="text-align: center;">图2.1 单声道波形图</div>
  </div>
  <div style="width: 100%;">
    <img src="https://tva1.sinaimg.cn/large/007S8ZIlly1gjnh61rt4yj30yp0u0atu.jpg">
    <div style="text-align: center;">图2.1 双声道波形图</div>
  </div>
</div>  

- ### 实验环境
  - `Python 3.8`

- ### 运行方式
  在 `/src` 中运行：
  ``` bash
  pip3 install -r requirements.txt
  python3 DrawWav.py *.wav
  ```
</br></br>

## 3. 使用 `Android Studio` 实现声波发送应用和声波接收应用
- ### 实验结果及使用说明
  <div style="width: 50%;">
    <img src="https://tva1.sinaimg.cn/large/007S8ZIlly1gjni08obwbj30nq140wku.jpg">
    <div style="text-align: center;">图3.1 应用界面</div>
  </div>
  </br>
  a) 录制音频  

  - 输入采样率
  - 点击 START RECORD 按钮开始录制
  - 点击 STOP RECORD 按钮结束录制
  <div style="display: flex;">
    <div style="width: 100%;">
      <img src="https://tva1.sinaimg.cn/large/007S8ZIlly1gjni2lwfb9j30nq140grx.jpg">
      <div style="text-align: center;">图3.a.1 开始录制</div>
    </div>
    <div style="width: 100%;">
      <img src="https://tva1.sinaimg.cn/large/007S8ZIlly1gjni3bapelj30nq140q99.jpg">
      <div style="text-align: center;">图3.a.2 停止录制</div>
    </div>
    <div style="width: 100%;">
      <img src="https://tva1.sinaimg.cn/large/007S8ZIlly1gjni58vrz5j30nq140dmc.jpg">
      <div style="text-align: center;">图3.a.3 录制结果</div>
    </div>
  </div>  
  </br>
  b) 播放本地音频
  
  - 选择本地音频文件
  - 点击 PLAY 按钮开始播放
  - 点击 PAUSE 按钮暂停
  - 拖动进度条以改变播放进度
  <div style="display: flex;">
    <div style="width: 100%;">
      <img src="https://tva1.sinaimg.cn/large/007S8ZIlly1gjnif0fs6pj30nq140te7.jpg">
      <div style="text-align: center;">图3.b.1 选择本地音频文件</div>
    </div>
    <div style="width: 100%;">
      <img src="https://tva1.sinaimg.cn/large/007S8ZIlly1gjniffwwvpj30nq14044u.jpg">
      <div style="text-align: center;">图3.b.2 播放</div>
    </div>
    <div style="width: 100%;">
      <img src="https://tva1.sinaimg.cn/large/007S8ZIlly1gjni3bapelj30nq140q99.jpg">
      <div style="text-align: center;">图3.b.3 暂停</div>
    </div>
  </div>  
  </br>

  c) 程序根据用户输入，生成并播放指定频率的音频

  - 输入频率、初始相位、音频时长、采样率
  - 点击 GENERATE 按钮 开始生成
  <div style="display: flex;">
    <div style="width: 100%;">
      <img src="https://tva1.sinaimg.cn/large/007S8ZIlly1gjnihvza2gj30nq14044y.jpg">
      <div style="text-align: center;">图3.c.1 开始生成</div>
    </div>
    <div style="width: 100%;">
      <img src="https://tva1.sinaimg.cn/large/007S8ZIlly1gjnii82cf0j30nq140q9d.jpg">
      <div style="text-align: center;">图3.b.3 生成完成</div>
    </div>
  </div>  
  </br>

- ### 实验环境
  - Android Studio 4.1
  - Java 8
  - 安卓模拟器 Pixel 2 XL API 24
  
- ### 运行方式
  - 将 `/src/AudioProcessor` 在 Android Studio 中打开