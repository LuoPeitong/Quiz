### liu-slide-questions适用于uni-app项目的滑动切换答题模版
### 本组件目前兼容微信小程序、H5
### 本组件支持小程序和H5，简单好用易修改、支持单选、多选、简答等各类题型，支持各类样式自定义
# --- 扫码预览、关注我们 ---

## 扫码关注公众号，查看更多插件信息，预览插件效果！ 

![](https://uni.ckapi.pro/uniapp/publicize.png)

``` 使用示例
<template>
	<view class="page-main">
		<liu-slide-questions :dataList="list" @submit="subData"></liu-slide-questions>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				list: [{
					id: '1',
					title: '这是一道单选题',
					problemType: 'SINGLE',
					children: [{
						alias: 'A',
						answer: '选项1',
						isSelect: 0 //这个是用来回显的字段(0:未选中；1:选中)(可不传)
					}, {
						alias: 'B',
						answer: '选项2',
						isSelect: 0 //这个是用来回显的字段(0:未选中；1:选中)(可不传)
					}, {
						alias: 'C',
						answer: '选项3',
						isSelect: 0 //这个是用来回显的字段(0:未选中；1:选中)(可不传)
					}],
				}, {
					id: '2',
					title: '这是一道多选题',
					problemType: 'MULTY',
					children: [{
						alias: 'A',
						answer: '选项1',
						isSelect: 0 //这个是用来回显的字段(0:未选中；1:选中)(可不传)
					}, {
						alias: 'B',
						answer: '选项2',
						isSelect: 0 //这个是用来回显的字段(0:未选中；1:选中)(可不传)
					}, {
						alias: 'C',
						answer: '选项3',
						isSelect: 0 //这个是用来回显的字段(0:未选中；1:选中)(可不传)
					}, {
						alias: 'D',
						answer: '选项4',
						isSelect: 0 //这个是用来回显的字段(0:未选中；1:选中)(可不传)
					}],
				}, {
					id: '3',
					title: '这是一道简答题',
					problemType: 'QUESTION',
					userAnswer: '' //这个是用来回显的字段(可不传)
				}], //问题列表数据
			}
		},
		methods: {
			//点击提交
			subData(data) {
				console.log('提交的数据：' + JSON.stringify(data))
			}
		}
	}
</script>
```

### 属性说明
| 名称                         | 类型            | 默认值               | 描述            |
| ----------------------------|--------------- | --------------------  | ---------------|
| dataList                    | Array          | []             	   | 问题列表数据
| @submit                     | Function       |              	       | 提交成功回调事件