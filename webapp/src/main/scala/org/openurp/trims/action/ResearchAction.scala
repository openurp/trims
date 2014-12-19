package org.openurp.trims.action

import org.beangle.webmvc.api.annotation.mapping
import org.beangle.webmvc.api.annotation.param
import org.beangle.data.jpa.dao.SqlBuilder
import org.openurp.base.Teacher

class ResearchAction extends AbsEamsAction {

  /**
   * 论文情况
   */
  @mapping(value = "thesis/{id}")
  def thesis(@param("id") id: String): String = {
    val teacher = entityDao.get(classOf[Teacher], new Integer(id))
    put("teacher", teacher)
    val sql = s"""select t.name tname,t.count,p.name pname,h.name hname,pr.name prname
		from research.thesis_harvests t
		join research.published_situations p on p.id = t.published_situation_id
		join research.harvest_types h on h.id = p.harvest_type_id
		join research.published_ranges pr on pr.id = p.published_range_id
		join research.researchers r on r.id = t.researcher_id
		join base.people pe on pe.id = r.person_id
		join base.teachers te on te.person_id = pe.id
		where te.id=${id}"""
    val query = SqlBuilder.sql(sql)
    val datas = entityDao .search(query)
    put("datas", datas)
    put("id", get("id"))
    forward()
  }
  
    /**
   * 专著情况
   */
  @mapping(value = "literature/{id}")
  def literature(@param("id") id: String): String = {
    val sql = s"""select l.name,l.count,l.publish_house,h.name hname
        from research.literatures l
        join research.harvest_types h on h.id = l.harvest_type_id
        join research.researchers r on r.id = l.researcher_id
        join base.people pe on pe.id = r.person_id
        join base.teachers te on te.person_id = pe.id
        where te.id=${id}"""
    val query = SqlBuilder.sql(sql)
    val datas = entityDao .search(query)
    put("datas", datas)
    forward()
  }
  

}