import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-terms-and-conditions',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIcon],
  templateUrl: './terms-and-conditions.html',
  styleUrls: ['./terms-and-conditions.css']
})
export class TermsAndConditionsComponent {}
