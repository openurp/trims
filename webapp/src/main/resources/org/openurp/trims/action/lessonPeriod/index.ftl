[@b.head/]
[#include "../trims.ftl"/]
<div style="margin:0; border:1px solid #ccc; border-radius:5px; padding:10px;">
  
  [@b.form action="!search" target="courseChartDiv" style="font-size:14px;"]
    [@termAndDepartCondition years=years teaching=1/]
    [@b.submit value="查询" class="btn btn-default"/]
  [/@]
  [@b.div id="courseChartDiv" href="!search?teaching=1"/]
</div>
[@b.foot/]