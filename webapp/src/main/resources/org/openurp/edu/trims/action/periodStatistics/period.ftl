[@b.head/]
[#include "../trims.ftl"/]
[#assign base=request.contextPath/]
[#if datas?size gt 0]
<div id="periodStatisticsChart" style="height:400px;">
</div>
<script src="${base}/static/js/echarts/echarts.js"></script>
<script type="text/javascript">
    // 路径配置
    require.config({
        paths: {
            echarts: '${base}/static/js/echarts'
        }
    });
        
    // 使用
    require(
        [
            'echarts',
            'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
        ],
        function (ec) {
            // 基于准备好的dom，初始化echarts图表
            var myChart = ec.init(document.getElementById('periodStatisticsChart')); 
            
            var option = {
                title : {
                    text: '[@beginYearAndEndYear/]  平均课时人数统计',
                },
                tooltip : {
                    trigger: 'axis'
                },
                calculable : true,
                xAxis : [
                    {
                        name:'平均课时',
                        type : 'category',
                        boundaryGap : false,
                        data : [[#list datas as data][#if data_index gt 0],[/#if]'${data[0]-5}~${data[0]+5}'[/#list]]
                    }
                ],
                yAxis : [
                    {
                        name:'教师人数',
                        type : 'value',
                        axisLabel : {
                            formatter: '{value}'
                        }
                    }
                ],
                series : [
                    {
                        name:'人数',
                        type:'line',
                        smooth:true,
                        data:[[#list datas as data][#if data_index gt 0],[/#if]${data[1]}[/#list]],
                        markPoint : {
                            data : [
                                {type : 'max', name: '最大值'},
                                {type : 'min', name: '最小值'}
                            ]
                        
                        }
                    }
                ]
            };
                    
    
            // 为echarts对象加载数据 
            myChart.setOption(option); 
        }
    );
</script>
[#else]
<div style="padding:100px; font-size:20px; text-align:center">暂无数据</div>
[/#if]
[@b.div href="!top10?beginYear=${beginYear!}&endYear=${endYear!}&teaching=${(teaching?string('1','0'))!}&departmentId=${(department.id)!}&teacherTypeId=${teacherTypeId!}"/]
[@b.foot/]