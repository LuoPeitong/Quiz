<template>
	<view>
		<wyb-popup ref="popup" type="bottom" height="600" :maskClickClose='false'  radius="12" :showCloseIcon="false">
		    <view class="popup-content">
		        <view class="popup_content_title"> {{pickerTittle}}
					<view  class="iconfont icon-close icon_top" @tap="handleHide()"></view>
				</view>
		        <view class="dialog_search_box">
					<view class="cuIcon-search text-grey dialog_search_text"></view>
					<input type="text" @input="inputValue" v-model="input_value"   placeholder="输入关键词进行搜索" class="dialog_search_inp"/>
				</view>
				<scroll-view scroll-y="true" class="select_item_box">
					<view class="select_item" hover-class="select_item_hover" @click="handleSelect(data)" v-for="(data,index) in options" :key="index">
						{{data}}
					</view>
				</scroll-view>
		    </view>
		</wyb-popup>
	</view>
</template>

<script>
	import wybPopup from './wyb-popup.vue'
	export default {
		components: {
	        wybPopup
		},
		props: {
			pickerList: {
				type: Array,
				default: () => []
			},
			pickerTittle:{
				type: String,
				default: '选择'
			}
		},
		watch: {
		    pickerList:{
				handler: function (val, oldVal) {
					this.list = val;
					this.options = val;
				},
				immediate: true, 
				deep: true // 深度监听
		    },
		},
		data() {
			return {
				input_value:'',
				list:[],
				options: [],
			}
		},
		onLoad() {
		},
		created() {
			this.options = this.list;
		},
		methods: {
			handleHide(){
				this.$refs.popup.hide()
				var a = this
				setTimeout(()=>{
					a.input_value =''
					a.options = a.list
				},100)
			},
			handleShow(){
				this.$refs.popup.show()
			},
			handleSelect(item){
				console.log(item)
				var a =this
				a.$emit('change', item)
				a.$refs.popup.hide()
				setTimeout(()=>{
					a.input_value =''
					a.options = a.list
				},100)
			},
			inputValue(e) {
				var a = this
				var inputTxt = e.detail.value;
				 a.options = []
				if (inputTxt.length == 0) {
					a.options = a.list;
				}else{
					a.options = a.list.filter(o => {
						return String(o).indexOf(e.detail.value) >= 0
					})
				}
			},
		}
	}
</script>

<style lang="scss">
@import "./iconfont.css";
.popup_content_title{
	padding-top: 30rpx;
	text-align: center;
	margin-bottom:30rpx;
	position: relative;
	font-size: 36rpx;
	.icon_top{
		font-size: 50rpx;
		position: absolute;
		top:20rpx;
		right: 20rpx;
	}
}

.dialog_search_box{
	width:680rpx;
	height: 80rpx;
	background-color: #f8f8f8;
	border-radius: 12rpx;
	margin: 0 auto;
	display: flex;
	align-items: center;
	margin-bottom: 20rpx;
	.dialog_search_text{
		margin-left: 30rpx;
		font-size: 30rpx;
	}
	.dialog_search_inp{
		height: 76rpx;
		line-height: 76rpx;
		text-align: left;
		padding-left: 20rpx;
		width: 590rpx;
	}
}
.select_item_box{
		width: 750rpx;
		height:380rpx ;
		color: #333;
		.select_item{
			width:680rpx;
			margin: 0 auto;
			padding: 16rpx 0;
			font-size: 30rpx;
			text-align: center;
		}
		.select_item_hover{
			background-color: #f8f8f8;
		}
}
</style>
