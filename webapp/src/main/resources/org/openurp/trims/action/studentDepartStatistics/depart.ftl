[@b.head/]
[#include "../echarts.ftl"/]
<div style="max-width:1000px; margin:10px auto; border:1px solid #ccc; border-radius:5px; padding:10px;">
  <script>
    function selectDepart(param){
      var departmentIds = [[#list datas as d][#if d_index gt 0],[/#if]${d[0]}[/#list]]
      bg.Go('${b.url('!departYear')}?departmentId='+departmentIds[param.dataIndex],'departYearDiv')
    }
  </script>
  [@echarts id="student_depart_chart" title="按学院统计" title2="点击图表显示某院系按年级统计"
    xname='院系' yname='学生人数' names=names values=values onclick='selectDepart'/]
  [@b.div id="departYearDiv"/]
</div>
[@b.foot/]