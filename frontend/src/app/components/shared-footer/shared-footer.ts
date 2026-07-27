import { Component } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { FaqService } from '../../services/faq.service';

@Component({
  selector: 'app-shared-footer',
  standalone: true,
  imports: [MatIcon],
  templateUrl: './shared-footer.html',
  styleUrls: ['./shared-footer.css']
})
export class SharedFooterComponent {
  constructor(private faqService: FaqService) {}

  openFaq(): void {
    this.faqService.open();
  }
}
