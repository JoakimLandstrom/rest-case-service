package se.plushogskolan.restcaseservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.plushogskolan.casemanagement.exception.AlreadyPersistedException;
import se.plushogskolan.casemanagement.exception.InternalErrorException;
import se.plushogskolan.casemanagement.exception.NoSpaceException;
import se.plushogskolan.casemanagement.exception.NotPersistedException;
import se.plushogskolan.casemanagement.exception.StatusConflictException;
import se.plushogskolan.casemanagement.model.WorkItem;
import se.plushogskolan.casemanagement.model.WorkItem.Status;
import se.plushogskolan.casemanagement.service.CaseService;
import se.plushogskolan.restcaseservice.exception.BadRequestException;
import se.plushogskolan.restcaseservice.exception.ConflictException;
import se.plushogskolan.restcaseservice.exception.NotFoundException;
import se.plushogskolan.restcaseservice.exception.WebInternalErrorException;
import se.plushogskolan.restcaseservice.model.DTOWorkItem;

@Component
public class WorkItemService {

	private final CaseService service;
	
	@Autowired
	public WorkItemService(CaseService service){
		this.service = service;
	}
	
	public WorkItem save(DTOWorkItem dtoWorkItem) {
		WorkItem workItem = DTOWorkItem.toEntity(dtoWorkItem);
		try {
			workItem = service.save(workItem);
		} catch(AlreadyPersistedException e) {
			throw new ConflictException(e.getMessage());
		} catch(InternalErrorException e) {
			throw new WebInternalErrorException(e.getMessage());
		}
		return workItem;
	}

	public WorkItem updateStatusById(Long workItemId, String status) {
		Status workItemStatus = stringToStatus(status);
		try {
			return service.updateStatusById(workItemId, workItemStatus);
		} catch(NotPersistedException e) {
			throw new NotFoundException(e.getMessage());
		} catch(InternalErrorException e) {
			throw new WebInternalErrorException(e.getMessage());
		}
	}

	public void deleteWorkItem(Long workItemId) {
		try {
			service.deleteWorkItem(workItemId);
		} catch(NotPersistedException e) {
			throw new NotFoundException(e.getMessage());
		} catch(InternalErrorException e) {
			throw new WebInternalErrorException(e.getMessage());
		}
	}

	public WorkItem addWorkItemToUser(Long workItemId, Long userId) {
		
		try {
			service.getUser(userId);
			return service.addWorkItemToUser(workItemId, userId);
		} catch(StatusConflictException | NoSpaceException e) {
			throw new ConflictException(e.getMessage());
		}catch(NotPersistedException e){
			throw new NotFoundException(e.getMessage());
		} catch(InternalErrorException e) {
			throw new WebInternalErrorException(e.getMessage());
		}
	}
	
	public List<WorkItem> searchWorkItemByDescription(String description, int page, int size) {
		try {
			return service.searchWorkItemByDescription(description, page, size);
		} catch(InternalErrorException e) {
			throw new WebInternalErrorException(e.getMessage());
		}
	}

	public List<WorkItem> getWorkItemsByStatus(String status, int page, int size) {
		Status workItemStatus = stringToStatus(status);
		try {
			return service.getWorkItemsByStatus(workItemStatus, page, size);
		} catch(InternalErrorException e) {
			throw new WebInternalErrorException(e.getMessage());
		}
	}
	
	public WorkItem getWorkItemById(Long id) {
		try {
			return service.getWorkItemById(id);
		} catch(NotPersistedException e) {
			throw new NotFoundException(e.getMessage());
		} catch(InternalErrorException e) {
			throw new WebInternalErrorException(e.getMessage());
		}
	}
	
	public DTOWorkItem getDTOWorkItemById(Long id) {
		WorkItem workItem = getWorkItemById(id);
		return DTOWorkItem.toDTO(workItem);
	}

	public List<WorkItem> getWorkItemsByTeamId(Long teamId, int page, int size) {
		try {
			return service.getWorkItemsByTeamId(teamId, page, size);
		} catch(InternalErrorException e) {
			throw new WebInternalErrorException(e.getMessage());
		}
	}

	public List<WorkItem> getWorkItemsByUserId(Long userId, int page, int size) {
		try {
			return service.getWorkItemsByUserId(userId, page, size);
		} catch(InternalErrorException e) {
			throw new WebInternalErrorException(e.getMessage());
		}
	}

	public List<WorkItem> getWorkItemsWithIssue(int page, int size) {
		try {
			return service.getWorkItemsWithIssue(page, size);
		} catch(InternalErrorException e) {
			throw new WebInternalErrorException(e.getMessage());
		}
	}
	
	public List<WorkItem> getAllWorkItems(int page, int size) {
		try {
			return service.getAllWorkItems(page, size);
		} catch(InternalErrorException e) {
			throw new WebInternalErrorException(e.getMessage());
		}
	}
	
	//TODO maybe implement, CaseService uses Date instead of LocalDate
//	public List<WorkItem> getWorkItemsByPeriodAndStatus(String status, LocalDate fromDate, 
//			LocalDate toDate, int page, int size) {
//		return service.getWorkItemsByPeriodAndStatus(stringToStatus(status), fromDate, toDate, page, size);
//	}
	
	private WorkItem.Status stringToStatus(String value) {
		WorkItem.Status status = null;
		
		if(value.equals("done") || value.equals("DONE"))
			status = WorkItem.Status.DONE;
		else if(value.equals("unstarted") || value.equals("UNSTARTED"))
			status = WorkItem.Status.UNSTARTED;
		else if(value.equals("started") || value.equals("STARTED"))
			status = WorkItem.Status.STARTED;
		else
			throw new BadRequestException(value + " is not a valid Status");
		
		return status;
	}
}
