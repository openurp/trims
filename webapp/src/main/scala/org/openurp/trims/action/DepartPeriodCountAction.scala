package org.openurp.trims.action

import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.edu.teach.lesson.Lesson
import org.beangle.data.jpa.dao.SqlBuilder
import org.beangle.data.jpa.dao.OqlBuilder
import org.beangle.commons.lang.Strings
import org.openurp.base.Department
/**
 * 各部门学期平均课时统计
 * */
class DepartPeriodCountAction extends AbsEamsAction[Lesson] {
  
  def index(): String = {
    put("years", getLessonYears())
    forward()
  }

  def period(): String = {
    val beginYear = getInt("beginYear")
    val endYear = getInt("endYear")
    val teaching = getBoolean("teaching")
    val sql = """select teach_depart_id, cast(avg(num) as int) num from 
		(select  l.teach_depart_id,lt.person_id,s.school_year, s.name, sum(c.period) num
		from edu_teach.lessons l 
		join edu_teach.lessons_teachers lt on lt.lesson_id=l.id 
		join base.semesters s on l.semester_id = s.id 
		join edu_teach.courses c on c.id = l.course_id
    join base.departments d on l.teach_depart_id = d.id
		where 1=1 """ + 
    (if(teaching.isDefined)s" and d.teaching = '${teaching.get}'"else"")+
        (if(beginYear.isDefined)s" and l.semester_id >= ${beginYear.get}"else"")+
        (if(endYear.isDefined)s" and l.semester_id <= '${endYear.get}'"else"")+
		"""group by l.teach_depart_id,s.school_year,s.name,lt.person_id
		order by lt.person_id) t
		group by teach_depart_id order by avg(num) desc"""
        put("beginYear", beginYear)
    put("endYear", endYear)
    put("teaching", teaching)
    val query = SqlBuilder.sql(sql)
    val datas = entityDao.search(query)
    val map = new collection.mutable.HashMap[String, String]
    entityDao.getAll(classOf[Department]).foreach( d => {
      map.put(d.id.toString(), if(Strings.isNotBlank(d.shortName)) d.shortName else d.name)
    })
    put("dempartmentMap", map)
    put("datas", datas)
    forward()
  }

}