[#ftl]
[@b.head/]
[#include "../nav.ftl"/]
[#if curYear??]
[@nav3 "year${curYear!}"]
  [#list years as v]
    <li class="year${v[0]}" [#if curYear?? && curYear == v[0]]class="active"[/#if]>[@b.a href="teaching!qualityYear?id=${staff.id}&year=${v[0]}"]${v[0]}(<span style="color: red;">${v[1]}</span>)[/@]</li>
  [/#list]
[/@]
<script>
  $(".year${curYear!}").addClass("active").siblings().removeClass("active");
</script>

<table class="gridtable">
  <tr>
    <th width="15%">学年学期</th>
    <th width="15%">课程代码</th>
    <th width="20%">课程名称</th>
    <th width="30%">面向学生</th>
    <th width="20%">评教得分</th>
  </tr>
  [#list lessons as lesson]
    <tr>
      <td>${lesson.semester.schoolYear}-${lesson.semester.name}</td>
      <td>${lesson.course.code}</td>
      <td>${lesson.course.name}</td>
      <td>${(lesson.teachClass.name)!}</td>
      <td>${lessonScoreMap[lesson.id?string]!}</td>
    </tr>
   [/#list]
</table>
[#else]
[/#if]
[@b.foot/]