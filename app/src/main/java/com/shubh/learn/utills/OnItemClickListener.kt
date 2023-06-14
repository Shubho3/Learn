package com.shubh.learn.utills

import com.shubh.learn.dto.NotesDto
import com.shubh.learn.dto.PicturesListDto
import com.shubh.learn.dto.PointsDto
import com.shubh.learn.dto.UserDto

 public interface OnItemClickListener {
    fun onItemClick(model: UserDto)
} public interface OnFriendItemClickListener {
    fun onItemClick(model: UserDto, points:String,type:String )

}
public interface OnNoteClickListener {
    fun onItemClick(model: NotesDto.Result)
}
public interface onRewardItemClick {
    fun onItemClick(model: PointsDto.Result)
}public interface onPictureItemClick {
    fun onItemClick(model: PicturesListDto.Result)
}