package gov.cms.ab2d.api.controller;

import com.okta.jwt.AccessTokenVerifier;
import com.okta.jwt.Jwt;
import com.okta.jwt.JwtVerificationException;
import com.okta.jwt.impl.DefaultJwt;
import gov.cms.ab2d.api.SpringBootApp;
import gov.cms.ab2d.api.security.JwtTokenAuthenticationFilter;
import gov.cms.ab2d.common.model.Contract;
import gov.cms.ab2d.common.model.Sponsor;
import gov.cms.ab2d.common.repository.*;
import gov.cms.ab2d.common.service.SponsorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.io.InputStream;
import java.time.*;
import java.util.List;
import java.util.Map;

import static gov.cms.ab2d.api.controller.TestUtil.TEST_USER;
import static gov.cms.ab2d.api.util.Constants.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootApp.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class AdminAPITests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SponsorService sponsorService;

    @Autowired
    private SponsorRepository sponsorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private TestUtil testUtil;

    private String token;

    @Before
    public void setup() throws IOException, InterruptedException, JwtVerificationException {
        contractRepository.deleteAll();
        jobRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        sponsorRepository.deleteAll();

        token = testUtil.setupToken(List.of(ADMIN_ROLE));
    }

    @Test
    public void testUploadOrgStructureFile() throws Exception {
        // Simple test to test API, more detailed test is found in service test
        String fileName = "parent_org_and_legal_entity_20191031_111812.xls";
        InputStream inputStream = this.getClass().getResourceAsStream("/" + fileName);

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", fileName, "application/vnd.ms-excel", inputStream);
        this.mockMvc.perform(MockMvcRequestBuilders.multipart(API_PREFIX + ADMIN_PREFIX + "/uploadOrgStructureReport")
                .file(mockMultipartFile).contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().is(202));
    }

    @Test
    public void testUploadAttestationFile() throws Exception {
        createData("S1234", "Med Contract", "Ins. Co.", 789);
        createData("S5660", "MEDCO CONTAINMENT LIFE AND MEDCO CONTAINMENT NY", "Ins. Co. 1", 321);
        createData("S5617", "CIGNA HEALTH AND LIFE INSURANCE COMPANY", "Ins. Co. 2", 456);

        // Simple test to test API, more detailed test is found in service test
        String fileName = "Attestation_Report_Sample.xlsx";
        InputStream inputStream = this.getClass().getResourceAsStream("/" + fileName);

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", fileName, "application/vnd.ms-excel", inputStream);
        this.mockMvc.perform(MockMvcRequestBuilders.multipart(API_PREFIX + ADMIN_PREFIX + "/uploadAttestationReport")
                .file(mockMultipartFile).contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().is(202));
    }

    // There has to be an existing contract in order for this report to be able to process data
    private void createData(String contractId, String contractName, String sponsorName, int hpmsId) {
        Contract contract = new Contract();
        contract.setContractNumber(contractId);
        contract.setContractName(contractName);
        contract.setAttestedOn(OffsetDateTime.of(LocalDateTime.of(2018, 10, 10, 9, 17), ZoneOffset.UTC));

        Sponsor sponsor = new Sponsor();
        sponsor.setHpmsId(hpmsId);
        sponsor.setLegalName(sponsorName);
        sponsor.setOrgName(sponsorName);
        sponsor.getContracts().add(contract);

        contract.setSponsor(sponsor);

        sponsorService.saveSponsor(sponsor);
    }
}
