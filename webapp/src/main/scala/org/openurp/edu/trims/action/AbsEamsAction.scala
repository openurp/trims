package org.openurp.edu.trims.action

import java.util.Calendar

import scala.collection.mutable.ListBuffer

import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.data.dao.SqlBuilder
import org.beangle.data.model.Entity
import org.beangle.webmvc.api.action.ActionSupport
import org.beangle.webmvc.entity.action.EntityAction
import org.openurp.base.model.Department
import org.openurp.code.BaseCode
import org.openurp.edu.base.model.Project
import org.openurp.edu.base.model.Student
import org.openurp.edu.lesson.model.Lesson

abstract class AbsEamsAction[T <: Entity[_]] extends ActionSupport with EntityAction[T] {

  protected def getProject() = {
    //FIXME
    entityDao.get(classOf[Project], 1)
  }

  protected def getDepartments() = {
    entityDao.findBy(classOf[Department], "teaching", Array(true))
  }

  protected def getAllDepartments() = {
    val query = OqlBuilder.from(classOf[Department])
    query.orderBy("code")
    entityDao.search(query)
  }

  protected def getDepartmentMap() = {
    val map = new collection.mutable.HashMap[String, String]
    entityDao.getAll(classOf[Department]).foreach(d => {
      map.put(d.id.toString(), if (d.shortName != null) d.shortName else d.name)
    })
    map
  }

  protected def getLessonTerms() = {
    val query = OqlBuilder.from(classOf[Lesson], "l")
    query.select("l.semester.id, l.semester.code")
    query.groupBy("l.semester.id, l.semester.code")
    query.orderBy("l.semester.id, l.semester.code")
    entityDao.search(query)
  }

  protected def getLessonYears() = {
    val query = OqlBuilder.from(classOf[Lesson], "l")
    query.select("l.semester.schoolYear, l.semester.schoolYear")
    query.groupBy("l.semester.schoolYear")
    query.orderBy("l.semester.schoolYear")
    entityDao.search(query).asInstanceOf[Seq[Array[Any]]]
  }

  protected def getStudentGrade() = {
    val query = OqlBuilder.from(classOf[Student], "s")
    query.select("s.grade")
    query.groupBy("s.grade")
    query.orderBy("s.grade")
    entityDao.search(query)
  }

  protected def getAvg(datas: Seq[Any])(f: Any => Double) = {
    val o = (for (d <- datas) yield f(d))
    o.sum / datas.length
  }

  protected def getStandardDeviation(datas: Seq[Any], avg: Double)(f: Any => Double) = {
    val o = (for (d <- datas) yield (Math.pow(f(d) - avg, 2)))
    Math.sqrt(o.sum / datas.length)
  }

  protected def where[T](query: OqlBuilder[T], condition: String, name: String, value: Option[Any]) {
    if (value.isDefined && Strings.isNotBlank(value.get.toString) &&
      !(value.get.getClass.isPrimitive() && value.get.asInstanceOf[AnyVal] == 0)) {
      put(name, value.get)
      query.where(condition, value.get)
    }
  }

  protected def putNamesAndValues(datas: Seq[Any]) {
    putNamesAndValues(datas, data => data(0))
  }

  protected def putNamesAndValues(datas: Seq[Any], nf: Array[Any] => Any) {
    val names = for (data <- datas) yield nf(data.asInstanceOf[Array[Any]])
    val values = for (data <- datas) yield data.asInstanceOf[Array[Any]](1)
    put("names", names)
    put("values", values)
    put("datas", datas)
  }

  protected def getYears(): ListBuffer[Int] = {
    val sql = """select min(_year)
        from(
        (select to_char(p.published_date,'YYYY') as _year
        from sin_harvest.thesis_harvests t
        join sin_harvest.published_situations p on p.id = t.published_situation_id)
        union
        (select to_char(publish_date,'YYYY') as _year
        from sin_harvest.literatures )) t"""
    val query = SqlBuilder.sql(sql)
    val startYear = new Integer(entityDao.search(query)(0).toString).toInt
    val years = new ListBuffer[Int]
    val curYear = Calendar.getInstance().get(Calendar.YEAR)
    for (year <- startYear to curYear) {
      years += year
    }
    years
  }

  protected def getCodes[T <: BaseCode](project: Project, clazz: Class[T]): Seq[T] = {
    val query = OqlBuilder.from(clazz, "code").where("code.beginOn <=:now and (code.endOn is null or code.endOn >=:now)", new java.util.Date)
    query.orderBy("code.code")
    entityDao.search(query)
  }

}