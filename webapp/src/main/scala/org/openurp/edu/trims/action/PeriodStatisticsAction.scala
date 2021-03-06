package org.openurp.edu.trims.action

import org.beangle.data.dao.SqlBuilder
import org.openurp.base.model.Department
import org.openurp.code.hr.model.StaffType
import org.openurp.code.job.model.PostType
import org.openurp.edu.lesson.model.Lesson
/**
 * 平均课时人数统计
 * */
class PeriodStatisticsAction extends  AbsEamsAction[Lesson]{
  
    def index(): String = {
    put("years", getLessonTerms())
    val departs = entityDao .findBy(classOf[Department], "teaching", Array(true))
    put("departs", departs)
    println( entityDao.getAll(classOf[PostType]))
    put("teacherTypes", entityDao.getAll(classOf[StaffType]))
    forward()
  }
  
  def period():String={
    val beginYear = getInt("beginYear")
    val endYear = getInt("endYear")
    val teaching = getBoolean("teaching")
    val departmentId = getInt("departmentId")
    val teacherTypeId = getInt("teacherTypeId")
    val sql = """select num * 10 + 5, count(*) from
		(select t.staff_id, cast(avg(num) / 10 as int) num from 
		(select  lt.staff_id,l.semester_id, sum(c.period) num
		from edu_teach.lessons l join base.departments d on l.teach_depart_id = d.id
		join edu_teach.lessons_teachers lt on lt.lesson_id=l.id 
    join edu_base.teachers te on te.id = lt.staff_id  join hr_base.staffs f on f.id = te.staff_id
    join hr_base.staff_post_infoes pi on f.post_head_id = pi.id
		join edu_base.courses c on c.id = l.course_id where f.state_id = 1 """ + 
    (if(teacherTypeId.isDefined)s" and pi.teacher_type_id = ${teacherTypeId.get}"else"")+
    (if(teaching.isDefined)s" and d.teaching = '${teaching.get}'"else"")+
		(if(beginYear.isDefined)s" and l.semester_id >= ${beginYear.get}"else"")+
		(if(endYear.isDefined)s" and l.semester_id <= '${endYear.get}'"else"")+
		(if(departmentId.isDefined)s" and l.teach_depart_id = '${departmentId.get}'"else"")+
		""" group by l.semester_id,lt.staff_id
		order by lt.staff_id) t
		group by staff_id order by avg(num) desc) t
		group by num 
		order by num"""
		put("beginYear", beginYear)
    put("endYear", endYear)
    put("teaching", teaching)
    put("teacherTypeId", teacherTypeId)
  	if(departmentId.isDefined){
  	  val departId = departmentId.get
  	  val department = entityDao .get(classOf[Department], departId)
  	  put("department",department)
  	}
    println(sql)
    val query = SqlBuilder.sql(sql)
    val datas = entityDao .search(query)
    put("datas", datas)
    forward()
  }
  
  def top10():String={
    val beginYear = getInt("beginYear")
    val endYear = getInt("endYear")
    val teaching = getBoolean("teaching")
    val departmentId = getInt("departmentId")
    val teacherTypeId = getInt("teacherTypeId")
    val sql="""	select  p.name p_name,s.code, sum(c.period) num, d.name d_name
		from edu_teach.lessons l 
		join edu_teach.lessons_teachers lt on lt.lesson_id=l.id 
    join edu_base.teachers te on te.id = lt.teacher_id
    join hr_base.staffs f on f.id = te.staff_id
    join ppl_base.people p on p.id = f.person_id
    join hr_base.staff_post_infoes pi on f.post_head_id = pi.id
	join base.departments dd on pi.department_id=dd.id--所属部门
	join base.departments d on l.teach_depart_id = d.id--开课院系
    join base.semesters s on l.semester_id = s.id
		join edu_base.courses c on c.id = l.course_id where f.state_id = 1"""+  
    (if(teacherTypeId.isDefined)s" and pi.teacher_type_id = ${teacherTypeId.get}"else"")+
    (if(teaching.isDefined)s" and d.teaching = '${teaching.get}'"else"")+
    (if(beginYear.isDefined)s" and s.id >= '${beginYear.get}'"else"")+
    (if(endYear.isDefined)s" and s.id <= '${endYear.get}'"else"")+
		(if(departmentId.isDefined)s" and l.teach_depart_id = '${departmentId.get}'"else"")+
		""" group by s.code,p.id, p.name, d.name
		order by num desc limit 10"""
  	if(departmentId.isDefined){
  	  val departId = departmentId.get
  	  val department = entityDao .get(classOf[Department], departId)
  	  put("department",department)
  	}
    val query = SqlBuilder.sql(sql)
    val datas = entityDao .search(query)
    put("beginYear", beginYear)
    put("endYear", endYear)
    put("datas", datas)
    forward()
  }

}