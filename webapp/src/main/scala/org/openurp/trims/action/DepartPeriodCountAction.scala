package org.openurp.trims.action

import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.edu.teach.lesson.Lesson
import org.beangle.data.jpa.dao.SqlBuilder
import org.beangle.data.jpa.dao.OqlBuilder
import org.beangle.commons.lang.Strings
import org.openurp.base.Department
import org.openurp.hr.base.code.TeacherType
/**
 * 各部门学期平均课时统计
 * */
class DepartPeriodCountAction extends AbsEamsAction[Lesson] {
  
  def index(): String = {
    put("years", getLessonYears())
    put("teacherTypes", entityDao.getAll(classOf[TeacherType]))
    forward()
  }

  def period(): String = {
    val beginYear = getInt("beginYear")
    val endYear = getInt("endYear")
    val teaching = getBoolean("teaching")
    val teacherTypeId = getInt("teacherTypeId")
    val sql = """select teach_depart_id, cast(avg(num) as int) num from 
		(select  l.teach_depart_id,lt.person_id,s.school_year, s.name, sum(c.period) num
		from edu_teach.lessons l 
		join edu_teach.lessons_teachers lt on lt.lesson_id=l.id 
		join base.semesters s on l.semester_id = s.id 
		join edu_teach.courses c on c.id = l.course_id
    join base.departments d on l.teach_depart_id = d.id
    join hr_base.staffs f on f.person_id = lt.person_id
    join hr_base.staff_post_infoes pi on pi.id=f.post_head_id
		where 1=1 and f.state_id=1 """ + 
    (if(teaching.isDefined)s" and d.teaching = '${teaching.get}'"else"")+
        (if(beginYear.isDefined)s" and l.semester_id >= ${beginYear.get}"else"")+
        (if(endYear.isDefined)s" and l.semester_id <= '${endYear.get}'"else"")+
        (if(teacherTypeId.isDefined)s" and pi.teacher_type_id = ${teacherTypeId.get}"else"")+
		"""group by l.teach_depart_id,s.school_year,s.name,lt.person_id
		order by lt.person_id) t
		group by teach_depart_id order by avg(num) desc"""
    val query = SqlBuilder.sql(sql)
    val datas = entityDao.search(query)
    val map = new collection.mutable.HashMap[String, String]
    entityDao.getAll(classOf[Department]).foreach( d => {
      map.put(d.id.toString(), if(Strings.isNotBlank(d.shortName)) d.shortName else d.name)
    })
    put("beginYear", beginYear)
    put("endYear", endYear)
    put("teaching", teaching)
    put("dempartmentMap", map)
    put("datas", datas)
    put("teacherTypeId", teacherTypeId)
    forward()
  }

}