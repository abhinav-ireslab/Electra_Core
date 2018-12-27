package com.ireslab.sendx.electra.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.ireslab.sendx.electra.entity.GstHsnCode;
public interface GstHsnCodeRepository extends PagingAndSortingRepository<GstHsnCode,Integer> {
	
	@Query("select ghc from GstHsnCode ghc where ghc.chapterNo=:chapterNo OR ghc.chapterNo='Others'")
	Page<GstHsnCode> findByChapterNo(@Param("chapterNo")String chapterNo,Pageable pageable);

	@Query("select ghc from GstHsnCode ghc where ghc.chapterNo='Others'")
	GstHsnCode findByChapterNoOther();

	
	/*@Query(value="select hsn from GstHsnCode hsn where hsn.hsnCode like %:searchData% OR hsn.description like %:searchData%  OR hsn.related_export_import_hsn_code like %:searchData% OR hsn.chapter_no like %:searchData% ")
	Page<GstHsnCode> findAllCustom(String searchData, Pageable pageable);*/

	
}
