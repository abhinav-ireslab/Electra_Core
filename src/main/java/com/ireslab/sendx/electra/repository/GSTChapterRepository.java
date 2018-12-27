package com.ireslab.sendx.electra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ireslab.sendx.electra.entity.GSTChapter;

public interface GSTChapterRepository extends CrudRepository<GSTChapter, Integer> {

	public GSTChapter findByChapterNumber(String itemTypeOrChapter);
	
	@Query("select gstChapter from GSTChapter gstChapter order by gstChapterId")
	public List<GSTChapter> findAllCust();

}
