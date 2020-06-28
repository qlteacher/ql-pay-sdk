<!DOCTYPE html>
<html lang="zh-CN">
    <head>
        <meta charset="utf-8">
        <link href="https://cdn.bootcss.com/twitter-bootstrap/3.0.1/css/bootstrap.min.css" rel="stylesheet">
        <link href="http://www.qltechdev.com/paydemo/css/index.css" rel="stylesheet">
    </head>

    <body>
        <div class="container">
            <div class="row clearfix">
                <div class="col-md-12 column">
                    <div class="jumbotron">
                        <h1>支付演示</h1>
                        <p>paysdk源码：<a target="_blank" href="https://git.qltechdev.com/lab/pay/pay-sdk">https://git.qltechdev.com/lab/pay/pay-sdk</a></p>
                        <p>微信支付官方开发文档：<a target="_blank" href="https://pay.weixin.qq.com/wiki/doc/api/index.html">https://pay.weixin.qq.com/wiki/doc/api/index.html</a></p>
                        <p>支付宝官方开发文档：<a target="_blank" href="https://docs.open.alipay.com/catalog">https://docs.open.alipay.com/catalog</a></p>
                    	<#if aliconf.test>
                    	<h2>当前使用的支付宝是沙箱环境 支付时请使用沙箱版客户端</h2>
                    	<img src="https://zos.alipayobjects.com/rmsportal/CaXHDDXkdxikcZP.png">
                    	
                    	<p>买家账号  iewyqf0061@sandbox.com</p>
						<p>登录密码  111111</p>
						<p>支付密码  111111</p>
                    	</#if>
                    	
                    </div>
                    <form role="form" id="payForm">
                        <div class="form-group">
                            <label for="orderId">订单号</label>
                            <input type="text" class="form-control" id="orderId" name="orderId"/>
                        </div>
                        <div class="form-group">
                            <label for="price">金额</label>
                            <input type="text" value="0.1" class="form-control" id="price" name="price"/>
                        </div>
                        <div class="form-group">
                            <label for="amount">支付方式</label>
                            <select class="form-control" id="payType">
                                <option value="ALIPAY_PC">支付宝PC<#if aliconf.test>(沙箱)<#else>(正式)</#if></option>
                                <option value="ALIPAY_WAP">支付宝WAP<#if aliconf.test>(沙箱)<#else>(正式)</#if></option>
                                <option value="ALIPAY_H5">支付宝H5<#if aliconf.test>(沙箱)<#else>(正式)</#if></option>
                                <option value="ALIPAY_APP">支付宝app<#if aliconf.test>(沙箱)<#else>(正式)</#if></option>
                                <option value="WXPAY_NATIVE">微信Native支付_模式二<#if wxiconf.test>(沙箱)<#else>(正式)</#if></option>
                                <option value="WXPAY_MWEB">微信H5支付<#if wxiconf.test>(沙箱)<#else>(正式)</#if></option>
                                <option value="WXPAY_MP">微信公众号支付<#if wxiconf.test>(沙箱)<#else>(正式)</#if></option>
                                <option value="WXPAY_MINI">微信小程序支付<#if wxiconf.test>(沙箱)<#else>(正式)</#if></option>
                                <option value="WXPAY_APP">微信APP支付<#if wxiconf.test>(沙箱)<#else>(正式)</#if></option>
                            </select>
                        </div>
                        <div class="form-group" id="openidForm">
                            <label for="openid">openid</label>
                            <input type="text" value="oB18luN4YLE3lEwmhIp22EsTSc-A" class="form-control" id="openid" name="openid"/>
                        </div>
                        <div class="form-group" id="buyerLogonIdForm" hidden>
                            <label for="buyerLogonId">买家支付宝账号（和buyer_id不能同时为空）</label>
                            <input type="text" class="form-control" id="buyerLogonId" name="buyerLogonId"/>
                        </div>
                        <div class="form-group" id="buyerIdForm" hidden>
                            <label for="buyerId">买家的支付宝唯一用户号（2088开头的16位纯数字）</label>
                            <input type="text" class="form-control" id="buyerId" value="2088102180797593" name="buyerId"/>
                        </div>
                        <button type="submit" onclick="" class="btn btn-default">提交</button>
                        
                        <button type="button" onclick="queryOrder()" class="btn btn-default">查询</button>
                        
                        <button type="button" onclick="cancelOrder()" class="btn btn-default">撤销</button>
                        
                        <button type="button" onclick="refundOrder()" class="btn btn-default">退款</button>
                        
                        <button type="button" onclick="closeOrder()" class="btn btn-default">关闭</button>
                        
                        <button type="button" onclick="downloadBill()" class="btn btn-default">获取昨天的对账单</button>
                        
                    </form>
                    <h5>响应结果</h5>
                    <pre id="response"></pre>
                    <h5>结果处理</h5>
                    <div id="result"></div>
                </div>

            </div>
        </div>

        <footer class="bs-docs-footer">
            <div class="container">
                <p>技术支持 www.qlteacher.com</p>
            </div>
        </footer>
    </body>

    <script src="https://cdn.bootcss.com/jquery/2.2.4/jquery.min.js"></script>
    <script src="https://cdn.bootcss.com/twitter-bootstrap/3.0.1/js/bootstrap.min.js"></script>
    <script src="https://cdn.bootcss.com/jquery.qrcode/1.0/jquery.qrcode.min.js"></script>
    <script>
    
    	function goonBridgeReady(prepay_id){
			var data = {
				prepayId: prepay_id
			};
	                
			$.ajax({
				type:'get',
				url:'prepayIdCreateSign',
				data:data,
				success:function(response){
					onBridgeReady(response.package,response.timeStamp,response.paySign,response.nonceStr)
				},
				error:function(response){
					$('#response').text(JSON.stringify(response, null, "\t"))
				}
			});
    	}
    
    	function onBridgeReady(package,timeStamp,sign,nonceStr){
    		if (typeof WeixinJSBridge == "undefined"){
			   if( document.addEventListener ){
			       document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
			   }else if (document.attachEvent){
			       document.attachEvent('WeixinJSBridgeReady', onBridgeReady); 
			       document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
			   }
			}
	
		   WeixinJSBridge.invoke(
		      'getBrandWCPayRequest', {
		         "appId":"${wxiconf.appid}",     //公众号名称，由商户传入     
		         "timeStamp":timeStamp,         //时间戳，自1970年以来的秒数     
		         "nonceStr":nonceStr, //随机串     
		         "package":package,     
		         "signType":"${wxiconf.signType}",         //微信签名方式：     
		         "paySign":sign //微信签名 
		      },
		      function(res){
		      $('#response').text(JSON.stringify(res, null, "\t"))
		      if(res.err_msg == "get_brand_wcpay_request:ok" ){
		      		// 使用以上方式判断前端返回,微信团队郑重提示：
		            //res.err_msg将在用户支付成功后返回ok，但并不保证它绝对可靠。
		      } 
		   }); 
		}
    
    	function queryOrder(){
    			$("#response").html('')
                $('#result').html('')
                
            	var data = {
                    orderId: $("#orderId").val(),
                    platform: $("#payType").val().split("_",1)[0]
                };
                
                $.ajax({
                    type:'get',
                    url:'query',
                    data:data,
                    success:function(response){
                    	$('#response').html(JSON.stringify(response, null, "\t"))
                    },
                    error:function(response){
                        $('#response').text(JSON.stringify(response, null, "\t"))
                    }
                });
       }
       
       function cancelOrder(){
       			$("#response").html('')
                $('#result').html('')
       
            	var data = {
                    orderId: $("#orderId").val(),
                    price: $("#price").val(),
                    platform: $("#payType").val().split("_",1)[0]
                };
                
                
                $.ajax({
                    type:'get',
                    url:'cancel',
                    data:data,
                    success:function(response){
                    	$('#response').html(JSON.stringify(response, null, "\t"))
                    },
                    error:function(response){
                        $('#response').text(JSON.stringify(response, null, "\t"))
                    }
                });
       }
       
       function refundOrder(){
       			$("#response").html('')
                $('#result').html('')
       
            	var data = {
                    orderId: $("#orderId").val(),
                    price: $("#price").val(),
                    platform: $("#payType").val().split("_",1)[0]
                };
                
                if("WXPAY"==$("#payType").val().split("_",1)[0]){
                    data.totalFee=$("#price").val();
                    data.outRefundNo= String(Math.floor(Math.random() * (999999 - 100000 + 1) + 100000)) ;
                }
                
                $.ajax({
                    type:'get',
                    url:'refund',
                    data:data,
                    success:function(response){
                    	$('#response').html(JSON.stringify(response, null, "\t"))
                    },
                    error:function(response){
                        $('#response').text(JSON.stringify(response, null, "\t"))
                    }
                });
       }
       
      function closeOrder(){
       			$("#response").html('')
                $('#result').html('')
       
            	var data = {
                    orderId: $("#orderId").val(),
                    price: $("#price").val(),
                    platform: $("#payType").val().split("_",1)[0]
                };
                
                
                $.ajax({
                    type:'get',
                    url:'close',
                    data:data,
                    success:function(response){
                    	$('#response').html(JSON.stringify(response, null, "\t"))
                    },
                    error:function(response){
                        $('#response').text(JSON.stringify(response, null, "\t"))
                    }
                });
       }
       
       function downloadBill(){
       			$("#response").html('')
                $('#result').html('')
            	
            	var data = {
                    billDate: getNowFormatTime,
                    platform: $("#payType").val().split("_",1)[0]
                };
                
                $.ajax({
                    type:'get',
                    url:'downloadBill',
                    data:data,
                    success:function(response){
                    	$('#response').text(response)
                    },
                    error:function(response){
                        $('#response').text(JSON.stringify(response, null, "\t"))
                    }
                });
       }
       
	   function getYesterdayFormatDay(nowDate) {
	        var char = "-";
	        if(nowDate == null){
	            nowDate = new Date();
	        }
	        var day = nowDate.getDate()-1;
	        var month = nowDate.getMonth() + 1;//注意月份需要+1
	        var year = nowDate.getFullYear();
	        //补全0，并拼接
	        return year + char + completeDate(month) + char +completeDate(day);
	    }
 
	    //获取当前时间，格式YYYY-MM-DD HH:mm:ss
	    function getNowFormatTime() {
	        var nowDate = new Date();

	        //补全0，并拼接
	        return getYesterdayFormatDay(nowDate);
	    }
 
	    //补全0
	    function completeDate(value) {
	        return value < 10 ? "0"+value:value;
	    }
       
        $(function () {
            genOrderId()

            //选择支付方式
            $("#payType").change(function (e) {
                genOrderId()
                $("#response").html('')
                $('#result').html('')
                //$("#openid").val('')

                var payType = $("#payType").val()
                //为公众号和小程序支付设置openid
                if (payType == 'WXPAY_MINI') {
                    //$("#openid").val('oB18luN4YLE3lEwmhIp22EsTSc-A')
                }else if (payType == 'WXPAY_MP') {
                    //$("#openid").val('oB18luN4YLE3lEwmhIp22EsTSc-A')
                }else if (payType == 'ALIPAY_H5') {
                    $("#openidForm").hide()
                    $("#buyerLogonIdForm").show()
                    $("#buyerIdForm").show()
                }else if (payType == 'WXPAY_NATIVE'){
                	<#if wxiconf.test>
                	$('#result').html('沙箱环境中 发起支付生成的二维码永远过期')
                	$("#price").val('1.01')
                	</#if>
                }else if (payType == 'WXPAY_MWEB'){
                	<#if wxiconf.test>
                	$('#result').html('沙箱环境中 无法正常进行跳转')
                	$("#price").val('1.01')
                	</#if>
                }
            })

            $("form").submit(function(e){
                var data = {
                    orderId: $("#orderId").val(),
                    price: $("#price").val(),
                    payType: $("#payType").val(),
                    openid: $("#openid").val(),
                    buyerLogonId: $("#buyerLogonId").val(),
                    buyerId: $("#buyerId").val()
                };
                $.ajax({
                    type:'post',
                    url:'pay',
                    data:data,
                    success:function(response){
                        //支付宝会跳转, 不要显示
                        if (($("#payType").val() != 'ALIPAY_PC')&&($("#payType").val() != 'ALIPAY_WAP')) {
                            $('#response').html(JSON.stringify(response, null, "\t"))
                        }

                        //根据支付方式处理
                        switch($("#payType").val()) {
                            case 'WXPAY_MWEB':
                            	<#if wxiconf.test>$('#result').html('沙箱环境中 发起支付生成的二维码永远过期')
                            		$('#result').html('结果是 ' + response.body + '<br />'
                                    + '沙箱环境不支持h5 返回的mweb_url是错误的');
                            	<#else>
                            		$('#result').html('结果是 ' + response.body + '<br />'
                                    + '由于微信H5支付会校验referer, 也就是商户平台申请h5支付时会填写的产品域名<br />'
                                    + '所以这里做了个重定向, 请复制链接<strong>用手机浏览器打开</strong><br />'
                                    + response.body.replace('https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb', 'http://' + document.domain + '/wxpay_mweb_redirect'));
                            	</#if>
                                
                                break
                            case 'WXPAY_NATIVE':
                            	$('#result').html('')
                                $('#result').qrcode(response.body)
                                break
                            case 'WXPAY_MP':
                            	console.log(response)
                            	goonBridgeReady(response.outTradeNo,response.body)
                            case 'WXPAY_MINI':
                                //$('#result').html('小程序/公众号支付，需要openid参数，暂时无法体验发起支付')
                                console.log(response)
                            	goonBridgeReady(response.outTradeNo,response.body)
                                break
                            case 'WXPAY_APP':
                                $('#result').html('app支付需由android/ios端发起')
                                break
                            case 'ALIPAY_APP':
                                $('#result').html('app支付需由android/ios端发起')
                                break
                            case 'ALIPAY_PC':
                                console.log(response.body)
                                $('#response').text(response.body.replace("document.forms[0]","document.getElementsByName('punchout_form')[0]").replace("action=","target=_blank action="))
                                $('#result').html(response.body.replace("document.forms[0]","document.getElementsByName('punchout_form')[0]").replace("action=","target=_blank action="))
                                break
                            case 'ALIPAY_WAP':
                                console.log(response.body)
                                $('#response').text(response.body.replace("document.forms[0]","document.getElementsByName('punchout_form')[0]").replace("action=","target=_blank action="))
                                $('#result').html(response.body.replace("document.forms[0]","document.getElementsByName('punchout_form')[0]").replace("action=","target=_blank action="))
                                break
                            case 'ALIPAY_H5':
                                console.log(response.body)
                                $('#result').html("h5发起支付文档 https://myjsapi.alipay.com/alipayjsapi/util/pay/tradePay.html")
                                break
                        }
                    },
                    error:function(response){
                        $('#response').text(JSON.stringify(response, null, "\t"))
                    }
                });
                return false;
            });

            //随机生成订单号
            function genOrderId() {
                $('#orderId').val(new Date().getTime() + String(Math.floor(Math.random() * (999999 - 100000 + 1) + 100000)));
            }
        });
    </script>
</html>