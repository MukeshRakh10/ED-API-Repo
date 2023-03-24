package in.mk.ed.service.impl;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.mk.ed.dto.ElibResponseDto;
import in.mk.ed.entity.CitizenApp;
import in.mk.ed.entity.CoTrigger;
import in.mk.ed.entity.DcCase;
import in.mk.ed.entity.EdEligDetails;
import in.mk.ed.entity.EducationDetails;
import in.mk.ed.entity.IncomeDetails;
import in.mk.ed.entity.KidsDetails;
import in.mk.ed.entity.PlanSelection;
import in.mk.ed.repository.CitizenAppRepository;
import in.mk.ed.repository.CoTriggerRepository;
import in.mk.ed.repository.DcCaseRepository;
import in.mk.ed.repository.EdEligRepository;
import in.mk.ed.repository.EducatonDetailsRepository;
import in.mk.ed.repository.IncomeRepository;
import in.mk.ed.repository.KidsRepository;
import in.mk.ed.repository.PlanSelectionRepository;
import in.mk.ed.service.IEDService;

@Service
public class EdServiceImpl implements IEDService {

	@Autowired
	private DcCaseRepository caseRepo;

	@Autowired
	private PlanSelectionRepository planRepo;

	@Autowired
	private IncomeRepository incomeRepo;

	@Autowired
	private KidsRepository kidsRepo;

	@Autowired
	private CitizenAppRepository citizenAppRepo;

	@Autowired
	private EducatonDetailsRepository eduRepo;

	@Autowired
	private CoTriggerRepository coRepo;

	@Autowired
	private EdEligRepository edRepo;

	boolean noKidsFlag = false;
	boolean ageFlag = true;

	@Override
	public ElibResponseDto determineEligibility(Long caseId) {

		ElibResponseDto elibRespDto = null;
		for (int i = 0; i < 5000; i++) {

			elibRespDto = new ElibResponseDto();
			Long planId = null;
			String planName = null;
			Long appId = null;
			Optional<DcCase> dcCase = caseRepo.findById(caseId);
			if (dcCase.isPresent()) {
				planId = dcCase.get().getPlanId();
				appId = dcCase.get().getAppId();
				caseId = dcCase.get().getCaseId();
			}
			elibRespDto.setCaseId(caseId + i);
			elibRespDto.setAppId(appId);
			Optional<PlanSelection> findById = planRepo.findById(planId);

			if (findById.isPresent()) {
				planName = findById.get().getPlanName();
			}

			IncomeDetails income = incomeRepo.findByCaseId(caseId);
			List<KidsDetails> kd = kidsRepo.findByCaseId(caseId);
			Optional<CitizenApp> citizenApp = citizenAppRepo.findById(appId);

			if ("SNAP".equalsIgnoreCase(planName)) {

				if (income.getMonthlySalIncome() >= 300) {
					elibRespDto.setPlanStatus("DENIED");
					elibRespDto.setDenialReason("High Income");
				}

			} else if ("CCAP".equalsIgnoreCase(planName)) {

				if (!kd.isEmpty()) {
					kd.forEach(d -> {
//					LocalDate dob = d.getKidAge();
//					LocalDate todayDate = LocalDate.now();
//					Period p = Period.between(dob, todayDate);// between give the period object
//					int year = p.getYears();
						int year = d.getKidAge();
						if (year > 16) {
							ageFlag = false;
						}

					});
				} else {
					elibRespDto.setDenialReason("No Kids Available ");
					ageFlag = false;
				}

				if (income.getMonthlySalIncome() > 300) {
					elibRespDto.setPlanStatus("DENIED");
					elibRespDto.setDenialReason("Not Eligible ");
				}

				if (!ageFlag) {
					elibRespDto.setDenialReason("Kids age greather than 16 ");
				}

				if (income.getMonthlySalIncome() > 300 & !ageFlag) {
					elibRespDto.setDenialReason("Not EligibleKids + Kids age greather than 16 ");
				}

			} else if ("MEDICAID".equalsIgnoreCase(planName)) {
				Long salaryIncome = income.getMonthlySalIncome();
				Long rentIncome = income.getRentIncome();
				Long propertyIncome = income.getPropertyIncome();

				if (salaryIncome > 300) {
					elibRespDto.setDenialReason("High Salary Income");
				}
				if (rentIncome > 0) {
					elibRespDto.setDenialReason("Rental Income Available");
				}
				if (propertyIncome > 0) {
					elibRespDto.setDenialReason("Property Income Available");
				}
				if (rentIncome > 0 && propertyIncome > 0) {
					elibRespDto.setDenialReason("Rental  + Property Income Available");
				}

				if (salaryIncome > 300 && rentIncome > 0 && propertyIncome > 0) {
					elibRespDto.setDenialReason("High Income + Rental  + Property Income Available");
				}

			} else if ("MEDICARE".equalsIgnoreCase(planName)) {

				CitizenApp cApp = citizenApp.get();
				LocalDate dob = cApp.getDob();
				LocalDate today = LocalDate.now();
				Period p = Period.between(dob, today);
				int year = p.getYears();
				if (year < 65) {
					elibRespDto.setDenialReason("Age < 65 Years");
				}

			} else if ("RIW".equalsIgnoreCase(planName)) {
				EducationDetails ed = eduRepo.findByCaseId(caseId);
				Integer graduationYear = ed.getEducationYear();
				if (graduationYear <= 0) {
					elibRespDto.setDenialReason("Not Graduated ....");
				}
				if (income.getMonthlySalIncome() > 0) {
					elibRespDto.setDenialReason("Already an Employee ....");
				}

			}

			elibRespDto.setPlanName(planName);
			if (elibRespDto.getDenialReason() != null) {
				elibRespDto.setPlanStatus("DENIED");
			} else {
				elibRespDto.setPlanStatus("APPROVED");
				elibRespDto.setPlanStartDate(LocalDate.now().plusDays(1));
				elibRespDto.setPlanEndDate(LocalDate.now().plusMonths(3));
				elibRespDto.setBenefitAmt(3500.0);
			}

			EdEligDetails edEligDetails = new EdEligDetails();
			BeanUtils.copyProperties(elibRespDto, edEligDetails);
			edRepo.save(edEligDetails);

			CoTrigger coEntity = new CoTrigger();
			coEntity.setCaseId(caseId + i);
			coEntity.setTrgStatus("pending");
			coRepo.save(coEntity);
		}

		return elibRespDto;
	}
}
